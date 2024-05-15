package de.nicetoapp.kapitalmeisterbackend.task.updater

import de.nicetoapp.kapitalmeisterbackend.data.GermanSeedDataRepository
import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanYearlyCpi
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanYearlyCpiRepository

class GermanYearlyCpiDataUpdater(
    private val germanSeedDataRepository: GermanSeedDataRepository,
    private val germanYearlyCpiRepository: GermanYearlyCpiRepository,
    seedDataSource: SeedDataSource,
    logRepository: DatabaseUpdateLogRepository
) : DataSourceUpdater(seedDataSource, logRepository) {

    override suspend fun updateDatabase(): Boolean {
        try {
            germanSeedDataRepository.getYearlyCpi()
                .collect { germanYearlyList ->
                    germanYearlyList as List<GermanYearlyCpi>
                    germanYearlyList.mapIndexed { index, cpiData ->
                        val previousValue = if (index > 0) germanYearlyList[index - 1].value else null
                        val change = if (previousValue != null && previousValue != 0.0) {
                            ((cpiData.value - previousValue) / previousValue) * 100
                        } else {
                            null
                        }
                        cpiData.change = change
                    }
                    germanYearlyCpiRepository.saveAll(germanYearlyList)
                }
            return true
        } catch (e: Exception) {
            // Ignored atm
        }
        return false
    }
}