package de.nicetoapp.kapitalmeisterbackend.model.common

import de.nicetoapp.kapitalmeisterbackend.util.CsvCompatible

data class SeedFile(val header: List<SeedFileHeader>, val content: List<String>) : CsvCompatible {

    override fun getHeader(): String = header.joinToString(separator = ";") { it.valueType.name }


    override fun getLines(): List<String> = content
}
