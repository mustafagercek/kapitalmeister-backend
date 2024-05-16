package de.nicetoapp.kapitalmeisterbackend.task.updater

import de.nicetoapp.kapitalmeisterbackend.model.common.SeedDataSource
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanCpiCategory
import de.nicetoapp.kapitalmeisterbackend.repository.DatabaseUpdateLogRepository
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanCpiCategoryRepository
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

class GermanCpiCategoryUpdater(
    private val germanCpiCategoryRepository: GermanCpiCategoryRepository,
    seedDataSource: SeedDataSource,
    logRepository: DatabaseUpdateLogRepository
) : DataSourceUpdater(seedDataSource, logRepository) {

    override suspend fun updateDatabase(): Boolean {
        try {
            val classLoader = javaClass.classLoader
            val resource = classLoader.getResource("static/cpi/categories/german_cpi_categories.csv")
                ?: throw FileNotFoundException("File not found in resources: static/cpi/categories/german_cpi_categories.csv")

            val cpiCategories = mutableListOf<GermanCpiCategory>()
            val categoryMap = mutableMapOf<String, GermanCpiCategory>()

            BufferedReader(InputStreamReader(resource.openStream())).use { reader ->
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

            germanCpiCategoryRepository.saveAll(cpiCategories)
        } catch (e: FileNotFoundException) {
            println("Update failed for GERMAN_CATEGORY: ${e.message}")
            return false
        } catch (e: Exception) {
            println("Update failed for GERMAN_CATEGORY: ${e.message}")
            return false
        }
        return true
    }
}