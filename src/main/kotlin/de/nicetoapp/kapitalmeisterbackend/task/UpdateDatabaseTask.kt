package de.nicetoapp.kapitalmeisterbackend.task

import de.nicetoapp.kapitalmeisterbackend.data.GermanSeedDataRepository
import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.common.DatabaseUpdateLog
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanCpiCategoryRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCategorizedCpiRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanMonthlyCpiRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanYearlyCpiRepository
import de.nicetoapp.kapitalmeisterbackend.task.updater.GermanCategorizedMonthlyCpiDataUpdater
import de.nicetoapp.kapitalmeisterbackend.task.updater.GermanCpiCategoryUpdater
import de.nicetoapp.kapitalmeisterbackend.task.updater.GermanMonthlyCpiDataUpdater
import de.nicetoapp.kapitalmeisterbackend.task.updater.GermanYearlyCpiDataUpdater
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UpdateDatabaseTask(
    private val seedRemoteRepository: GermanSeedDataRepository,
    private val germanYearlyCpiRepository: GermanYearlyCpiRepository,
    private val germanMonthlyCpiRepository: GermanMonthlyCpiRepository,
    private val germanMonthlyCategorizedCpiRepository: GermanMonthlyCategorizedCpiRepository,
    private val logRepository: DatabaseUpdateLogRepository,
    private val germanCpiCategoryRepository: GermanCpiCategoryRepository
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @PostConstruct
    fun onStartup() {
        executeUpdaters()
    }

    @Scheduled(cron = "0 0 12 ? * MON")
    fun execute() {
        executeUpdaters()
    }

    private fun executeUpdaters() {
        coroutineScope.launch {
            SeedDataSource.entries.forEach { seedDataSource ->
                val databaseUpdater = when (seedDataSource) {
                    SeedDataSource.GERMAN_MONTHLY_CPI -> GermanMonthlyCpiDataUpdater(
                        seedRemoteRepository,
                        germanMonthlyCpiRepository,
                        seedDataSource,
                        logRepository
                    )

                    SeedDataSource.GERMANY_YEARLY_CPI -> GermanYearlyCpiDataUpdater(
                        seedRemoteRepository,
                        germanYearlyCpiRepository,
                        seedDataSource,
                        logRepository
                    )

                    SeedDataSource.GERMAN_CATEGORY -> GermanCpiCategoryUpdater(
                        germanCpiCategoryRepository,
                        seedDataSource,
                        logRepository
                    )

                    SeedDataSource.GERMAN_CAT_MONTHLY_CPI -> GermanCategorizedMonthlyCpiDataUpdater(
                        seedRemoteRepository,
                        germanMonthlyCategorizedCpiRepository,
                        seedDataSource,
                        logRepository
                    )
                }

                if (databaseUpdater.isUpdateNeeded()) {
                    println("Database update needed for $seedDataSource")
                    coroutineScope.launch {
                        if (databaseUpdater.updateDatabase()) {
                            println("Update was successful for $seedDataSource")
                            val updateLog = DatabaseUpdateLog(
                                logTimestamp = LocalDateTime.now(),
                                seedDataSource = seedDataSource,
                                status = DatabaseUpdateLog.Status.SUCCESS
                            )
                            logRepository.save(updateLog)
                        } else {
                            println("Update failed for $seedDataSource")
                        }
                    }
                }
            }
        }
    }
}