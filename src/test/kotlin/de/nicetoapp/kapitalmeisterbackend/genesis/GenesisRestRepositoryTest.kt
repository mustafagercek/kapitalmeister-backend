package de.nicetoapp.kapitalmeisterbackend.genesis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.nicetoapp.kapitalmeisterbackend.util.GenesisContentGenerator
import de.nicetoapp.kapitalmeisterbackend.model.dto.GenesisTableResponse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class GenesisRestRepositoryTest {

    companion object {
        private const val YEARLY_CPI_FILE_NAME = "genesis-mock-response-6111-0001.json"
        private const val MONTHLY_CPI_FILE_NAME = "genesis-mock-response-6111-0002.json"
        private const val YEARLY_CATEGORY_CPI_FILE_NAME = "genesis-mock-response-6111-0003.json"
        private const val MONTHLY_CATEGORY_CPI_FILE_NAME = "genesis-mock-response-6111-0004.json"
        private const val RESOURCES_DIR = "src/test/kotlin/de/nicetoapp/kapitalmeisterbackend/mocks/"
    }

    @Test
    fun `test yearly cpi`() = runTest {
        val genesisSeedFile =
            GenesisContentGenerator(getMockedResponse(YEARLY_CPI_FILE_NAME)).generateGenesisSeedFile()
        assert(genesisSeedFile.header.size == 2)
        assert(genesisSeedFile.content.size == 4)
    }

    @Test
    fun `test monthly cpi`() = runTest {
        val genesisSeedFile =
            GenesisContentGenerator(getMockedResponse(MONTHLY_CPI_FILE_NAME)).generateGenesisSeedFile()
        assert(genesisSeedFile.header.size == 3)
        assert(genesisSeedFile.content.size == 60)
    }

    @Test
    fun `test category cpi`() = runTest {
        val genesisSeedFile =
            GenesisContentGenerator(getMockedResponse(YEARLY_CATEGORY_CPI_FILE_NAME)).generateGenesisSeedFile()
        assert(genesisSeedFile.header.size == 3)
        assert(genesisSeedFile.content.size == 3969)
    }

    @Test
    fun `test monthly category cpi`() = runTest {
        val genesisSeedFile =
            GenesisContentGenerator(getMockedResponse(MONTHLY_CATEGORY_CPI_FILE_NAME)).generateGenesisSeedFile()
        assert(genesisSeedFile.header.size == 4)
        assert(genesisSeedFile.content.size == 5292)
    }

    private fun getMockedResponse(mockFileName: String): GenesisTableResponse {
        val resourcePath = Paths.get(RESOURCES_DIR, mockFileName)
        val json = Files.readString(resourcePath)
        return jacksonObjectMapper().readValue(json)
    }

}
