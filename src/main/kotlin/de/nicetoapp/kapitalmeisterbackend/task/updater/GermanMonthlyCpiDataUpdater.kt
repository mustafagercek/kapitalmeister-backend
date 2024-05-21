package de.nicetoapp.kapitalmeisterbackend.task.updater

import de.nicetoapp.kapitalmeisterbackend.data.GermanSeedDataRepository
import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCpi
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCpiRepository
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GermanMonthlyCpiDataUpdater(
    private val germanSeedDataRepository: GermanSeedDataRepository,
    private val germanMonthlyCpiRepository: GermanMonthlyCpiRepository,
    seedDataSource: SeedDataSource,
    logRepository: DatabaseUpdateLogRepository
) : DataSourceUpdater(seedDataSource, logRepository) {

    override suspend fun updateDatabase(): Boolean {
        try {
            val startYear = 1991
            val endYear = LocalDate.now().year
            val step = 5
            val allCpiData = mutableListOf<GermanMonthlyCpi>()

            for (year in startYear..endYear step step) {
                val toYear = (year + step - 1).coerceAtMost(endYear)
                germanSeedDataRepository.getMonthlyCpi(year, toYear)
                    .map { it }
                    .collect { cpiList ->
                        allCpiData.addAll(cpiList)
                    }
            }

            val sortedList = allCpiData.sortedWith(compareBy({ it.year }, { it.month }))
            sortedList.mapIndexed { index, cpiData ->
                cpiData.value?.let {
                    val previousMonthValue =
                        if (index > 0 && (sortedList[index - 1].year == cpiData.year || (cpiData.month == 0 && sortedList[index - 1].year == cpiData.year - 1))) {
                            sortedList[index - 1].value
                        } else null

                    val previousYearValue =
                        sortedList.find { it.year == cpiData.year - 1 && it.month == cpiData.month }?.value

                    val monthOverMonthChange = if (previousMonthValue != null && previousMonthValue != 0.0) {
                        ((cpiData.value - previousMonthValue) / previousMonthValue) * 100
                    } else {
                        null
                    }

                    val yearOverYearChange = if (previousYearValue != null && previousYearValue != 0.0) {
                        ((cpiData.value - previousYearValue) / previousYearValue) * 100
                    } else {
                        null
                    }

                    cpiData.previousMonthValue = previousMonthValue
                    cpiData.previousYearValue = previousYearValue
                    cpiData.momChange = monthOverMonthChange
                    cpiData.yoyChange = yearOverYearChange
                }
            }

            val distinctList = sortedList.distinctBy { Pair(it.year, it.month) }

            germanMonthlyCpiRepository.saveAll(distinctList)
            return true
        } catch (e: Exception) {
            // Ignored atm
        }
        return false
    }
}