package de.nicetoapp.kapitalmeisterbackend.controller

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCategorizedCpi
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanYearlyCpi
import de.nicetoapp.kapitalmeisterbackend.model.response.cpi.GermanCategorizedMonthlyCpiListResponse
import de.nicetoapp.kapitalmeisterbackend.model.response.cpi.GermanCategorizedMonthlyCpiResponse
import de.nicetoapp.kapitalmeisterbackend.model.response.cpi.GermanCategorizedMonthlyCpiResponseFull
import de.nicetoapp.kapitalmeisterbackend.model.response.cpi.GermanMonthlyCpiResponse
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCategorizedCpiRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCpiRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanYearlyCpiRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cpi/german")
class GermanCpiController(
    private val updateLogRepository: DatabaseUpdateLogRepository,
    private val monthlyCategorizedCpiRepository: GermanMonthlyCategorizedCpiRepository
) {

    @Autowired
    private lateinit var yearlyCpiRepository: GermanYearlyCpiRepository

    @Autowired
    private lateinit var monthlyCpiRepository: GermanMonthlyCpiRepository

    @GetMapping("/yearly")
    fun getAllGermanYearlyCpi(): List<GermanYearlyCpi> {
        return yearlyCpiRepository.findAll()
    }

    @GetMapping("/monthly/categories/latest")
    fun getLatestMonthlyCategories(): GermanCategorizedMonthlyCpiListResponse {
        val sort = Sort.by(Sort.Order.asc("germanCpiCategory"), Sort.Order.asc("year"), Sort.Order.asc("month"))
        val lastUpdateLog = updateLogRepository.findLatestBySource(SeedDataSource.GERMAN_MONTHLY_CPI)

        val latestYear = monthlyCategorizedCpiRepository.findMaxYear()
        val latestMonth = monthlyCategorizedCpiRepository.findMaxMonthByYearWithValue(latestYear)
        val cpiList =
            monthlyCategorizedCpiRepository.findByYearAndMonthAndCategoryWithNoParent(latestYear, latestMonth, sort)
                .map { cpiEntity ->
                    GermanCategorizedMonthlyCpiResponse(
                        cpiEntity.year,
                        cpiEntity.month,
                        cpiEntity.germanCpiCategory!!.id,
                        cpiEntity.value,
                        cpiEntity.previousMonthValue,
                        cpiEntity.momChange,
                        cpiEntity.previousYearValue,
                        cpiEntity.yoyChange,
                    )

                }
        return GermanCategorizedMonthlyCpiListResponse(latestYear, latestYear, cpiList, lastUpdateLog?.logTimestamp)
    }

    @GetMapping("/monthly/categories/latest/full")
    fun getLatestMonthlyCategoriesFull(
        @RequestParam(required = true) includeChildren: Boolean = false,
    ): GermanCategorizedMonthlyCpiListResponse {
        val sort = Sort.by(Sort.Order.asc("germanCpiCategory"), Sort.Order.asc("year"), Sort.Order.asc("month"))
        val lastUpdateLog = updateLogRepository.findLatestBySource(SeedDataSource.GERMAN_MONTHLY_CPI)
        val latestYear = monthlyCategorizedCpiRepository.findMaxYear()
        val latestMonth = monthlyCategorizedCpiRepository.findMaxMonthByYearWithValue(latestYear)
        val cpiList =
            monthlyCategorizedCpiRepository.findByYearAndMonthAndCategoryWithNoParent(latestYear, latestMonth, sort)
                .map { cpiEntity ->
                    buildFullMonthlyCategorizedCpiResponse(
                        cpiEntity,
                        latestYear,
                        latestMonth,
                        sort
                    )
                }
        return GermanCategorizedMonthlyCpiListResponse(latestYear, latestYear, cpiList, lastUpdateLog?.logTimestamp)
    }

    @GetMapping("/monthly")
    fun getAllGermanMonthlyCpi(
        @RequestParam(required = false) startYear: Int?,
        @RequestParam(required = false) endYear: Int?
    ): GermanMonthlyCpiResponse {
        val sort = Sort.by(Sort.Order.asc("year"), Sort.Order.asc("month"))
        val monthlyCpis = if (startYear != null && endYear != null) {
            monthlyCpiRepository.findByYearBetween(startYear, endYear, sort)
        } else {
            monthlyCpiRepository.findAll(sort)
        }

        val lastUpdateLog = updateLogRepository.findLatestBySource(SeedDataSource.GERMAN_MONTHLY_CPI)
        val lastUpdate = lastUpdateLog?.logTimestamp

        return GermanMonthlyCpiResponse(
            monthlyCpis = monthlyCpis,
            lastUpdate = lastUpdate
        )
    }

    private fun buildFullMonthlyCategorizedCpiResponse(
        cpiEntity: GermanMonthlyCategorizedCpi,
        year: Int,
        month: Int,
        sort: Sort,
    ): GermanCategorizedMonthlyCpiResponseFull {
        val childEntities =
            monthlyCategorizedCpiRepository.findChildCategories(year, month, cpiEntity.germanCpiCategory!!.id)
        val childResponses = childEntities.map { buildFullMonthlyCategorizedCpiResponse(it, year, month, sort) }

        return GermanCategorizedMonthlyCpiResponseFull(
            cpiEntity.year,
            cpiEntity.month,
            cpiEntity.germanCpiCategory.id,
            cpiEntity.value,
            cpiEntity.previousMonthValue,
            cpiEntity.momChange,
            cpiEntity.previousYearValue,
            cpiEntity.yoyChange,
            childResponses
        )
    }

}
