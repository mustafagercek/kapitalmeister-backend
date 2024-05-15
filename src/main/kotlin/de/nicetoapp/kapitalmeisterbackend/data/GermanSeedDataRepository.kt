package de.nicetoapp.kapitalmeisterbackend.data

import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.*
import de.nicetoapp.kapitalmeisterbackend.repository.cpi.GermanCpiCategoryRepository
import de.nicetoapp.kapitalmeisterbackend.service.GenesisService
import de.nicetoapp.kapitalmeisterbackend.util.GenesisContentGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths

@Repository
class GermanSeedDataRepository(
    private val genesisService: GenesisService,
    private val germanCpiCategoryRepository: GermanCpiCategoryRepository
) {

    fun getYearlyCpi(): Flow<List<YearlyCpi>> {
        return getYearlyGermanCpi()
    }

    fun getMonthlyCpi(startYear: Int, endYear: Int): Flow<List<GermanMonthlyCpi>> {
        val additionalParams = mapOf(
            START_YEAR_PARAM to startYear.toString(),
            END_YEAR_PARAM to endYear.toString()
        )
        return genesisService.fetchTable(
            "61111-0002", additionalParams
        ).map { tableResponse ->
            GenesisContentGenerator(tableResponse).generateGenesisSeedFile().content.map { contentRow ->
                GermanMonthlyCpi.fromGenesisTableFileRow(contentRow)
            }
        }
    }

    fun getMonthlyCategorizedCpi(startYear: Int, endYear: Int): Flow<List<GermanMonthlyCategorizedCpi>> {
        val additionalParams = mapOf(
            START_YEAR_PARAM to startYear.toString(),
            END_YEAR_PARAM to endYear.toString()
        )
        return genesisService.fetchTable(
            "61111-0004", additionalParams
        ).map { tableResponse ->
            GenesisContentGenerator(tableResponse).generateGenesisSeedFile().content.map { contentRow ->
                val categoryId = contentRow.split(";")[0].trim()
                val category = germanCpiCategoryRepository.findById(categoryId).orElseThrow {
                    IllegalArgumentException("Category ID $categoryId not found")
                }


                GermanMonthlyCategorizedCpi.fromGenesisTableFileRow(contentRow, category)
            }
        }
    }

    private fun getYearlyGermanCpi(): Flow<List<YearlyCpi>> {
        return genesisService.fetchTable("61111-0001").map { tableResponse ->
            GenesisContentGenerator(tableResponse).generateGenesisSeedFile().content.map { contentRow ->
                GermanYearlyCpi.fromGenesisTableFileRow(contentRow)
            }
        }
    }

    fun getGermanCpiCategories(): Flow<List<GermanCpiCategory>> {
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

        return flowOf(cpiCategories)
    }


    companion object {
        private const val START_YEAR_PARAM = "startyear"
        private const val END_YEAR_PARAM = "endyear"
    }
}