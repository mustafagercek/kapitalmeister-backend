package de.nicetoapp.kapitalmeisterbackend.util

import de.nicetoapp.kapitalmeisterbackend.model.common.Country
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.io.IOException

class SeedFileFinder(private val seedDirectory: String, private val country: Country) {

    fun getFile(): Path? {
        val path = Paths.get(seedDirectory)
        try {
            Files.newDirectoryStream(path).use { paths ->
                return paths
                    .filter { filePath -> !Files.isDirectory(filePath) }
                    .find { filePath ->
                        val fileName = filePath.fileName.toString()
                        val fileLocale = fileName.split("_").firstOrNull()
                        fileLocale?.equals(country.name, ignoreCase = true) ?: false
                    }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
