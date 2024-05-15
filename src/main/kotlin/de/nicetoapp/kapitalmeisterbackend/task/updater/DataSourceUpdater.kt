package de.nicetoapp.kapitalmeisterbackend.task.updater

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import java.time.LocalDateTime

abstract class DataSourceUpdater(
    private val dataSource: SeedDataSource,
    private val logRepository: DatabaseUpdateLogRepository
) {

    /**
     * Checks if an update is needed based on the last log entry and the refresh interval.
     *
     * @return true if an update is needed, false otherwise
     */
    open fun isUpdateNeeded(): Boolean {
        val logEntry = logRepository.findLatestBySource(dataSource) ?: return true
        if (dataSource.refreshInterval == -1) {
            return false
        }
        return logEntry.logTimestamp.isBefore(LocalDateTime.now().minusWeeks(dataSource.refreshInterval.toLong()))
    }

    abstract suspend fun updateDatabase(): Boolean


}