package de.nicetoapp.kapitalmeisterbackend.task.updater

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanCpiCategory
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanCpiCategoryRepository
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths

class GermanCpiCategoryUpdater(
    private val germanCpiCategoryRepository: GermanCpiCategoryRepository,
    seedDataSource: SeedDataSource,
    logRepository: DatabaseUpdateLogRepository
) : DataSourceUpdater(seedDataSource, logRepository) {

    override suspend fun updateDatabase(): Boolean {
        try {
            val filePath = Paths.get("src/main/resources/static/cpi/categories/german_cpi_categories.csv")
            val cpiCategories = mutableListOf<GermanCpiCategory>()
            val categoryMap = mutableMapOf<String, GermanCpiCategory>()

            if (Files.exists(filePath)) {
                BufferedReader(FileReader(filePath.toFile())).use { reader ->
                    val header = reader.readLine()
                    val headerColumns = header.split(";")
                    val idIndex = headerColumns.indexOf("id")
                    val nameIndex = headerColumns.indexOf("name")

                    reader.forEachLine { line ->
                        val columns = line.split(";")
                        val id = columns[idIndex]
                        val name = columns[nameIndex]
                        val parentId = if (id.count { it == '-' } == 2) null else id.dropLast(1)
                        val parentCategory: GermanCpiCategory? = if (parentId != null) categoryMap[parentId] else null

                        val category = GermanCpiCategory(id, categoryName = name, parentCategory = parentCategory)
                        categoryMap[id] = category
                        cpiCategories.add(category)
                    }
                }
            } else {
                throw FileNotFoundException("File not found at $filePath")
            }
            val sortedCategories = cpiCategories.sortedBy { it.id.length }

            germanCpiCategoryRepository.saveAll(sortedCategories)
            return true
        } catch (e: Exception) {
            // Ignored atm
        }
        return false
    }
}