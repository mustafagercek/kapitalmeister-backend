package de.nicetoapp.kapitalmeisterbackend.task.updater

import de.nicetoapp.kapitalmeisterbackend.data.GermanSeedDataRepository
import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCategorizedCpi
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCategorizedCpiRepository
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GermanCategorizedMonthlyCpiDataUpdater(
    private val germanSeedDataRepository: GermanSeedDataRepository,
    private val germanMonthlyCategorizedCpiRepository: GermanMonthlyCategorizedCpiRepository,
    seedDataSource: SeedDataSource,
    logRepository: DatabaseUpdateLogRepository
) : DataSourceUpdater(seedDataSource, logRepository) {

    override suspend fun updateDatabase(): Boolean {
        try {
            val startYear = 2022
            val endYear = LocalDate.now().year
            val step = 1
            val allCpiData = mutableListOf<GermanMonthlyCategorizedCpi>()

            for (year in startYear..endYear step step) {
                val toYear = (year + step - 1).coerceAtMost(endYear)
                println("Fetching data from $year to $toYear")

                germanSeedDataRepository.getMonthlyCategorizedCpi(year, toYear)
                    .map { it }
                    .collect { cpiList ->
                        allCpiData.addAll(cpiList)
                    }
            }

            val groupedByCategory = allCpiData.groupBy { it.germanCpiCategory?.id }
            val updatedCpiData = mutableListOf<GermanMonthlyCategorizedCpi>()

            groupedByCategory.forEach { (categoryId, cpiList) ->
                val sortedList = cpiList.sortedWith(compareBy({ it.year }, { it.month }))

                sortedList.forEachIndexed { index, cpiData ->
                    cpiData.value?.let { currentValue ->
                        val previousMonthValue =
                            if (index > 0) sortedList[index - 1].value else null

                        val previousYearValue =
                            sortedList.find { it.year == cpiData.year - 1 && it.month == cpiData.month }?.value

                        val monthOverMonthChange = if (previousMonthValue != null && previousMonthValue != 0.0) {
                            ((currentValue - previousMonthValue) / previousMonthValue) * 100
                        } else {
                            null
                        }

                        val yearOverYearChange = if (previousYearValue != null && previousYearValue != 0.0) {
                            ((currentValue - previousYearValue) / previousYearValue) * 100
                        } else {
                            null
                        }

                        cpiData.momChange = monthOverMonthChange
                        cpiData.yoyChange = yearOverYearChange
                        cpiData.previousYearValue = previousYearValue
                        cpiData.previousMonthValue = previousMonthValue
                    }
                    updatedCpiData.add(cpiData)
                }
            }
            germanMonthlyCategorizedCpiRepository.saveAll(updatedCpiData)
            return true
        } catch (e: Exception) {
            println(e)
            // Ignored atm
        }
        return false
    }
}