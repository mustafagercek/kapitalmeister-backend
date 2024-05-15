package de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german

import de.nicetoapp.kapitalmeisterbackend.util.GenesisHelper
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "german_monthly_categorized_cpi")
@IdClass(GermanMonthlyCategorizedCpi.CategorizedMonthlyCpiId::class)
class GermanMonthlyCategorizedCpi(
    @Id
    val year: Int = 0,
    @Id
    val month: Int = 0,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    val germanCpiCategory: GermanCpiCategory? = null,

    val value: Double? = null,

    var previousMonthValue: Double? = null,
    var momChange: Double? = null,

    var previousYearValue: Double? = null,
    var yoyChange: Double? = null
) {

    companion object {
        fun fromGenesisTableFileRow(contentRow: String, category: GermanCpiCategory): GermanMonthlyCategorizedCpi {
            val lineComponents = contentRow.split(";")
            val year: Int = lineComponents[1].toInt()
            val monthName = lineComponents[2]
            val month = GenesisHelper.germanMonthMap[monthName] ?: throw IllegalArgumentException("Invalid month name: $monthName")
            val value: Double? = try {
                lineComponents[3].replace(",", ".").toDouble()
            } catch (e: NumberFormatException) {
                null
            }
            return GermanMonthlyCategorizedCpi(year, month, category, value)
        }
    }

    data class CategorizedMonthlyCpiId(
        val year: Int = 0,
        val month: Int = 0,
        val germanCpiCategory: String = ""
    ) : Serializable
}
