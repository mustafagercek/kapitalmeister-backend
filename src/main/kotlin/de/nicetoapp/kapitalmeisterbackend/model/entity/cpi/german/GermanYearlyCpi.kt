package de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table


@MappedSuperclass
abstract class YearlyCpi(
    @Id
    val year: Int,
    val value: Double,
    var change: Double? = null
)

@Entity
@Table(name = "german_yearly_cpi")
class GermanYearlyCpi(
    year: Int = 0,
    value: Double = 0.0,
    change: Double? = null
) : YearlyCpi(year, value, change) {

    companion object {
        fun fromGenesisTableFileRow(contentRow: String): GermanYearlyCpi {
            val lineComponents = contentRow.split(";")
            val year: Int = lineComponents[0].toInt()
            val value: Double = lineComponents[1].replace(",", ".").toDouble()
            return GermanYearlyCpi(year, value, null)
        }
    }
}