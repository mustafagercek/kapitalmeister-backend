package de.nicetoapp.kapitalmeisterbackend.model.response.cpi

import java.time.LocalDateTime

data class GermanCategorizedMonthlyCpiListResponse(
    val startYear: Int,
    val endYear: Int,
    val monthlyCpiList: List<GermanCategorizedMonthlyCpiResponse>,
    val lastUpdate: LocalDateTime?,
)

open class GermanCategorizedMonthlyCpiResponse(
    val year: Int,
    val month: Int,
    val category: String,
    val value: Double? = null,
    val previousMonthValue: Double? = null,
    val momChange: Double? = null,
    val previousYearValue: Double? = null,
    val yoyChange: Double? = null,
)

class GermanCategorizedMonthlyCpiResponseFull(
    year: Int,
    month: Int,
    category: String,
    value: Double? = null,
    previousMonthValue: Double? = null,
    momChange: Double? = null,
    previousYearValue: Double? = null,
    yoyChange: Double? = null,
    val childs: List<GermanCategorizedMonthlyCpiResponse>
) : GermanCategorizedMonthlyCpiResponse(year, month, category, value, previousMonthValue, momChange, previousYearValue, yoyChange)