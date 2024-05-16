package de.nicetoapp.kapitalmeisterbackend.model.response.cpi

import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCpi
import java.time.LocalDateTime

data class GermanMonthlyCpiResponse(
    val monthlyCpis: List<GermanMonthlyCpi>,
    val lastUpdate: LocalDateTime?,
)
