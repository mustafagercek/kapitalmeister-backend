package de.nicetoapp.kapitalmeisterbackend.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.IOException

class SeedFileGenerator(private val csvCompatible: CsvCompatible) {

    fun createFile(fileName: String, dir: String): Flow<Boolean> = flow {
        val path = "$dir/$fileName.csv"
        val file = File(path)
        try {
            file.parentFile.mkdirs()  // Ensure the directory exists
            file.bufferedWriter().use { writer ->
                writer.write(csvCompatible.getHeader())
                writer.newLine()
                val lines = csvCompatible.getLines()
                val lastLineIndex = lines.size - 1
                lines.forEachIndexed { index, line ->
                    writer.write(line)
                    if (index != lastLineIndex) {
                        writer.newLine()
                    }
                }
            }
            emit(true)  // Emit true on successful write
        } catch (e: IOException) {
            emit(false)  // Emit false on failure to write
        }
    }
}