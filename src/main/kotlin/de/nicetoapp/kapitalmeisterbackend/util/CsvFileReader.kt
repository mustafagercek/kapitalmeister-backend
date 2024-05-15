package de.nicetoapp.kapitalmeisterbackend.util

import java.io.File
import java.io.FileNotFoundException

class CsvFileReader(private val pathName: String, private val delimiter: String = ";") {
    fun readData(): List<Map<String, String>> {
        val entries = mutableListOf<Map<String, String>>()
        var columnNames: List<String>? = null
        try {
            val file = File(pathName)
            file.forEachLine { line ->
                if (columnNames == null) {
                    columnNames = line.split(delimiter)
                } else {
                    val columns = line.split(delimiter)
                    val entry = mutableMapOf<String, String>()
                    for (i in columns.indices) {
                        if (i < columnNames!!.size) {
                            entry[columnNames!![i]] = columns[i]
                        }
                    }
                    entries.add(entry)
                }
            }
        } catch (e: FileNotFoundException) {

        }
        return entries
    }
}