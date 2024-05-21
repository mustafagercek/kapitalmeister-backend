package de.nicetoapp.kapitalmeisterbackend.model.rest.cpi.german

import java.time.LocalDateTime

data class GermanMonthlyCpiResponse(
    val monthlyCpis: List<GermanMonthlyCpiEntry>,
    val lastUpdate: LocalDateTime?,
) {

    data class GermanMonthlyCpiEntry(
        val year: Int,
        val month: Int,
        val value: Double,
        val previousMonthValue: Double? = null,
        val momChange: Double? = null,
        val previousYearValue: Double? = null,
        val yoyChange: Double? = null
    )

}
