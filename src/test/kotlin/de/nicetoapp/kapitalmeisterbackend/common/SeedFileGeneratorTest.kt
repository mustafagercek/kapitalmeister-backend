package de.nicetoapp.kapitalmeisterbackend.common

class SeedFileGeneratorTest {

    companion object {
        private const val YEARLY_CPI_FILE_NAME = "genesis-mock-response-6111-0001.json"
        private const val MONTHLY_CPI_FILE_NAME = "genesis-mock-response-6111-0002.json"
        private const val YEARLY_CATEGORY_CPI_FILE_NAME = "genesis-mock-response-6111-0003.json"
        private const val MONTHLY_CATEGORY_CPI_FILE_NAME = "genesis-mock-response-6111-0004.json"

        @JvmStatic
        fun dataSources(): List<String> = listOf(
            YEARLY_CPI_FILE_NAME,
            MONTHLY_CPI_FILE_NAME,
            YEARLY_CATEGORY_CPI_FILE_NAME,
            MONTHLY_CATEGORY_CPI_FILE_NAME
        )
    }

//    @ParameterizedTest
//    @MethodSource("dataSources")
//    fun `execute writes correct CSV file for each data source`(fileName: String) = runTest {
//        val response = GenesisService(FakeGenesisWebClient(fileName), mock())
//            .fetchTable("")
//            .first()
//        val csvCompatible = GenesisContentGenerator(response).generateGenesisSeedFile()
//
//        val dir = "src/test/kotlin/de/nicetoapp/kapitalmeisterbackend/generated/"
//        val testFileName = fileName.substringBeforeLast(".")
//        val useCase = SeedFileGenerator(csvCompatible)
//
//        useCase.execute(testFileName, dir)
//
//        val expectedFile = File(dir, testFileName)
//        assertTrue(expectedFile.exists(), "The file $testFileName should exist.")
//    }
}
