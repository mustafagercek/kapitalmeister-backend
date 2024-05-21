package de.nicetoapp.kapitalmeisterbackend.controller

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.rest.cpi.german.GermanCategorizedMonthlyCpiResponse
import de.nicetoapp.kapitalmeisterbackend.model.rest.cpi.german.GermanMonthlyCpiResponse
import de.nicetoapp.kapitalmeisterbackend.model.rest.cpi.german.GermanYearlyCpiResponse
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCategorizedCpiRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCategorizedCpiSpecifications
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCpiRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanYearlyCpiRepository
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.RoundingMode

@RestController
@RequestMapping("/api/cpi/german")
class GermanCpiController(
    private val updateLogRepository: DatabaseUpdateLogRepository,
    private val monthlyCategorizedCpiRepository: GermanMonthlyCategorizedCpiRepository,
    private val yearlyCpiRepository: GermanYearlyCpiRepository,
    private val monthlyCpiRepository: GermanMonthlyCpiRepository
) {

    @GetMapping("/yearly")
    fun getAllGermanYearlyCpi(): List<GermanYearlyCpiResponse> {
        return yearlyCpiRepository.findAll()
            .map { GermanYearlyCpiResponse(it.year, it.value, it.change) }
    }

    @GetMapping("/monthly/latest")
    fun getLatestMonthly(
    ): GermanMonthlyCpiResponse.GermanMonthlyCpiEntry {
        val latestMonthly = monthlyCpiRepository.findLatestMonthWithValue()!!
        return GermanMonthlyCpiResponse.GermanMonthlyCpiEntry(
            latestMonthly.year,
            latestMonthly.month,
            latestMonthly.value!!,
            latestMonthly.previousMonthValue,
            latestMonthly.momChange,
            latestMonthly.previousYearValue,
            latestMonthly.yoyChange
        )
    }

    @GetMapping("/monthly/categories")
    fun getCategorizedMonthly(
        @RequestParam(required = true) childrenDepth: Int = 0,
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
        @RequestParam(required = false) catId: String?,
    ): GermanCategorizedMonthlyCpiResponse {
        val sort = Sort.by(Sort.Order.asc("germanCpiCategory"), Sort.Order.asc("year"), Sort.Order.asc("month"))
        val lastUpdateLog = updateLogRepository.findLatestBySource(SeedDataSource.GERMAN_MONTHLY_CPI)
        val spec = GermanMonthlyCategorizedCpiSpecifications.byYearMonthAndCategory(
            year,
            month,
            catId,
            childrenDepth
        )
        val resultList = monthlyCategorizedCpiRepository.findAll(spec, sort)
            .map { cpiEntity ->
                GermanCategorizedMonthlyCpiResponse.GerCatMoCpiEntry(
                    cpiEntity.year,
                    cpiEntity.month,
                    GermanCategorizedMonthlyCpiResponse.GermanCpiCategoryResponse(
                        cpiEntity.germanCpiCategory!!.id,
                        cpiEntity.germanCpiCategory.categoryName!!
                    ),
                    formatValue(cpiEntity.value),
                    formatValue(cpiEntity.previousMonthValue),
                    formatValue(cpiEntity.momChange),
                    formatValue(cpiEntity.previousYearValue),
                    formatValue(cpiEntity.yoyChange),
                )
            }
        return GermanCategorizedMonthlyCpiResponse(resultList, lastUpdateLog?.logTimestamp)
    }

    private fun formatValue(value: Double?): Double? {
        return value?.let {
            BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
        }
    }

}
