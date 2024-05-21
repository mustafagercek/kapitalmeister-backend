package de.nicetoapp.kapitalmeisterbackend.model.rest.cpi.german

data class GermanYearlyCpiResponse(
    val year: Int,
    val value: Double,
    val change: Double?
)