package de.nicetoapp.kapitalmeisterbackend.util

interface CsvCompatible {

    fun getHeader(): String
    fun getLines(): List<String>
}