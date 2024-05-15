package de.nicetoapp.kapitalmeisterbackend.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GenesisTableResponse(
    @JsonProperty("Object") val tableData: TableData?,
    @JsonProperty("Parameter") val parameterData: ParameterData?,
    ) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TableData(
        @JsonProperty("Content") val content: String?,
        @JsonProperty("Structure") val structure: TableStructure?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ParameterData(
        @JsonProperty("startyear") val startyear: Int?,
        @JsonProperty("endyear") val endyear: Int?,

        )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TableStructure(
        @JsonProperty("Columns") val columns: List<TableValue>?,
        @JsonProperty("Rows") val rows: List<TableValue>?,
        @JsonProperty("Head") val head: Header? // You need to define HeadType
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TableValue(
        @JsonProperty("Code") val code: ValueCode? = null,
        @JsonProperty("Content") val content: String?,
        @JsonProperty("Type") val type: String?,
        @JsonProperty("Values") val values: String? = null,
        @JsonProperty("Selected") val selected: String?,
        @JsonProperty("Structure") val structure: List<TableValue>? = null,
        @JsonProperty("Updated") val updated: String?,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Header(
        @JsonProperty("Code") val code: String?,
        @JsonProperty("Content") val content: String?,
        @JsonProperty("Type") val type: String?,
        @JsonProperty("Values") val values: String?,
        @JsonProperty("Selected") val selected: String?,
        @JsonProperty("Structure") val rows: List<TableValue>?,
    )

    enum class ValueCode {
        @JsonProperty("JAHR")
        YEAR,

        @JsonProperty("MONAT")
        MONTH,

        @JsonProperty("PREIS1")
        INDEX,

        @JsonProperty("DINSG")
        DINSG,

        @JsonProperty("CC13A5")
        CC13A5
    }
}





