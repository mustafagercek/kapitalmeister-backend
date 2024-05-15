package de.nicetoapp.kapitalmeisterbackend.model.entity.common

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "database_update_log")
data class DatabaseUpdateLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    @Enumerated(EnumType.STRING) val seedDataSource: SeedDataSource? = null,
    val logTimestamp: LocalDateTime = LocalDateTime.now(),
    @Enumerated(EnumType.STRING) val status: Status? = null
) {

    enum class Status {
        SUCCESS,
        FAILURE
    }
}


