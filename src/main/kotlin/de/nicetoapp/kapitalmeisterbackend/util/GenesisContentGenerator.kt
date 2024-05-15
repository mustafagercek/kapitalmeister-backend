package de.nicetoapp.kapitalmeisterbackend.util

import de.nicetoapp.kapitalmeisterbackend.model.dto.GenesisTableResponse
import de.nicetoapp.kapitalmeisterbackend.model.common.SeedFile
import de.nicetoapp.kapitalmeisterbackend.model.common.SeedFileHeader

class GenesisContentGenerator(private val genesisResponse: GenesisTableResponse) {

    private val tableLines = genesisResponse.tableData!!.content!!.split("\n")

    companion object {
        private const val DEFAULT_NUMBER_OF_VALUES = 1;
        private const val DEFAULT_EMPTY_VALUE = " "
    }

    fun generateGenesisSeedFile(): SeedFile {
        val seedHeader = getSeedHeaders()
        val seedContent = createSeedContent(seedHeader)
        return SeedFile(seedHeader, seedContent)
    }

    private fun getSeedHeaders(): List<SeedFileHeader> {
        val headers = mutableSetOf<SeedFileHeader>()

        val responseRows = genesisResponse.tableData?.structure?.rows ?: emptyList()
        val responseColumns = genesisResponse.tableData?.structure?.columns ?: emptyList()
        val responseHeaders = genesisResponse.tableData?.structure?.head

        headers.addAll(createSeedHeader(responseRows, SeedFileHeader.StructureType.ROW))
        headers.addAll(createSeedHeader(responseColumns, SeedFileHeader.StructureType.COLUMN))
        headers.addAll(
            createSeedHeader(
                responseHeaders?.rows ?: emptyList(), SeedFileHeader.StructureType.HEADER
            )
        )

        return headers.toList()
    }

    private fun createSeedContent(headers: List<SeedFileHeader>): List<String> {
        val rawContentLines = getRawContentLines(headers.filter { it.structureType == SeedFileHeader.StructureType.ROW }
            .map { it.valueType })

        val columnHeader = headers.filter { it.structureType == SeedFileHeader.StructureType.COLUMN }
        val rowHeader = headers.filter { it.structureType == SeedFileHeader.StructureType.ROW }

        var numberOfValues = DEFAULT_NUMBER_OF_VALUES
        var columnOffset = rowHeader.size
        val columnHeaderContentMap = mutableMapOf<SeedFileHeader.ValueType, MutableMap<Int, String>>()

        if(columnHeader.any { it.valueType == SeedFileHeader.ValueType.YEAR }) {
            columnHeader.forEach { header ->
                if (header.valueType != SeedFileHeader.ValueType.IGNORED) {
                    val columnContentMap = mutableMapOf<Int, String>()
                    tableLines.first { line ->
                        line.split(";").all { it.trim().isEmpty() || getMatcher(header.valueType).invoke(it) }
                    }.split(";").forEachIndexed { index, columnValue ->
                        if (columnValue.isNotEmpty() && columnOffset == DEFAULT_NUMBER_OF_VALUES) {
                            columnOffset = index
                        }
                        columnContentMap[index] = columnValue
                    }
                    numberOfValues = columnContentMap.values.filter { it.isNotEmpty() }.size
                    columnHeaderContentMap[header.valueType] = columnContentMap;
                }
            }
        }
        val resultValues = mutableListOf<String>()
        rawContentLines.forEach { rawLine ->
            val lineComponents = rawLine.split(";")
            repeat(numberOfValues) { number ->
                if (lineComponents.size >= headers.size) {
                    var line = ""
                    rowHeader.forEachIndexed { rowIndex, _ ->
                        line += "${if (rowIndex != 0) ";" else ""}${lineComponents[rowIndex]}"
                    }
                    columnHeader.forEach {
                        line += if (numberOfValues > 1) ";${columnHeaderContentMap[it.valueType]!![columnOffset + number]}"
                        else ";${lineComponents[columnOffset + number]}"
                    }

                    if (numberOfValues > 1) line += ";${lineComponents[columnOffset + number]}"
                    if (line.isNotEmpty()) resultValues.add(line)
                }
            }
        }
        return resultValues
    }

    private fun createSeedHeader(
        tableValues: List<GenesisTableResponse.TableValue>, structureType: SeedFileHeader.StructureType
    ): List<SeedFileHeader> {
        val headers = mutableListOf<SeedFileHeader>()
        tableValues.forEach { tableValue ->
            val valueType = SeedFileHeader.ValueType.mapGenesisValue(tableValue.code)
            if (valueType != SeedFileHeader.ValueType.IGNORED) {
                headers.add(SeedFileHeader(valueType, structureType))
            }
            tableValue.structure?.let {
                headers.addAll(createSeedHeader(it, structureType))
            }
        }
        return headers;
    }

    private fun getRawContentLines(rowValueTypes: List<SeedFileHeader.ValueType>): List<String> {
        return tableLines.filter { line ->
            val lineParts = line.split(";")
            if (lineParts.size < rowValueTypes.size) {
                false;
            } else {
                val isValidLine = rowValueTypes.filterIndexed { index, valueType ->
                    !getMatcher(valueType).invoke(lineParts[index])
                }.isEmpty()
                isValidLine
            }
        }.map { it.replace("...", "-") }
    }

    private fun getMatcher(valueType: SeedFileHeader.ValueType): (String) -> Boolean {
        when (valueType) {
            SeedFileHeader.ValueType.YEAR -> {
                return { line -> line.matches("""\d{4}.*""".toRegex()) }
            }

            SeedFileHeader.ValueType.MONTH -> {
                val monthsRegex =
                    """^(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember)(;(Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember))*$""".toRegex()
                return { line -> monthsRegex.matches(line) }
            }

            SeedFileHeader.ValueType.BASKET_ITEM -> {
                return { line: String -> line.matches("""CC\d{2}-\d+.*""".toRegex()) }
            }

            SeedFileHeader.ValueType.PRICE_INDEX -> {
                return { line: String -> line.matches("""\d{2,3},\d{1}""".toRegex()) }
            }

            else -> {
                return { line -> true }
            }
        }
    }
}