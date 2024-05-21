package de.nicetoapp.kapitalmeisterbackend.model.rest.cpi.german

import java.time.LocalDateTime

data class GermanCategorizedMonthlyCpiResponse(
    val monthlyCpiList: List<GerCatMoCpiEntry>,
    val lastUpdate: LocalDateTime?,
) {
    data class GerCatMoCpiEntry(
        val year: Int,
        val month: Int,
        val category: GermanCpiCategoryResponse,
        val value: Double? = null,
        val previousMonthValue: Double? = null,
        val momChange: Double? = null,
        val previousYearValue: Double? = null,
        val yoyChange: Double? = null,
    )

    data class GermanCpiCategoryResponse(val id: String, val name: String)
}

