package de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german

import de.nicetoapp.kapitalmeisterbackend.util.GenesisHelper
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@IdClass(GermanMonthlyCpi.MonthlyCpiId::class)
@Table(name = "german_monthly_cpi")
class GermanMonthlyCpi(
    @Id
    val year: Int = 0,
    @Id
    val month: Int = 0,
    val value: Double? = 0.0,
    var previousMonthValue: Double? = null,
    var momChange: Double? = null,
    var previousYearValue: Double? = null,
    var yoyChange: Double? = null
) {

    companion object {
        fun fromGenesisTableFileRow(contentRow: String): GermanMonthlyCpi {
            val lineComponents = contentRow.split(";")
            val year: Int = lineComponents[0].toInt()
            val monthName = lineComponents[1]
            val month = GenesisHelper.germanMonthMap[monthName]
                ?: throw IllegalArgumentException("Invalid month name: $monthName")
            val value: Double? = try {
                lineComponents[2].replace(",", ".").toDouble()
            } catch (e: NumberFormatException) {
                null
            }
            return GermanMonthlyCpi(year, month, value)
        }
    }

    data class MonthlyCpiId(
        val year: Int = 0,
        val month: Int = 0,
    ) : Serializable
}

