package de.nicetoapp.kapitalmeisterbackend.repository

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.common.DatabaseUpdateLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DatabaseUpdateLogRepository : JpaRepository<DatabaseUpdateLog, Long> {

    @Query("SELECT d FROM DatabaseUpdateLog d WHERE d.seedDataSource = :seedDataSource ORDER BY d.logTimestamp DESC LIMIT 1")
    fun findLatestBySource(@Param("seedDataSource") seedDataSource: SeedDataSource): DatabaseUpdateLog?
}