package de.nicetoapp.kapitalmeisterbackend.mocks

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.nicetoapp.kapitalmeisterbackend.model.dto.GenesisTableResponse
import org.mockito.Mockito.*
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec
import reactor.core.publisher.Flux
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

class FakeGenesisWebClient(responseFilename: String) : WebClient {
    companion object {
        private const val RESOURCES_DIR = "src/test/kotlin/de/nicetoapp/kapitalmeisterbackend/mocks/"
    }

    private val requestHeaderMock: RequestHeadersUriSpec<*> = mock(RequestHeadersUriSpec::class.java)
    private val responseSpecMock: WebClient.ResponseSpec = mock(WebClient.ResponseSpec::class.java)

    init {
        `when`(requestHeaderMock.uri(any(URI::class.java))).thenReturn(requestHeaderMock)
        `when`(requestHeaderMock.retrieve()).thenReturn(responseSpecMock)
        val resourcePath = Paths.get(RESOURCES_DIR, responseFilename)
        val json = Files.readString(resourcePath)
        val response: GenesisTableResponse = jacksonObjectMapper().readValue(json)
        `when`(responseSpecMock.bodyToFlux(GenesisTableResponse::class.java)).thenReturn(Flux.just(response))
    }

    override fun get(): RequestHeadersUriSpec<*> {
        return requestHeaderMock
    }
    override fun head(): RequestHeadersUriSpec<*> = mock(RequestHeadersUriSpec::class.java)
    override fun post(): WebClient.RequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec::class.java)
    override fun put(): WebClient.RequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec::class.java)
    override fun patch(): WebClient.RequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec::class.java)
    override fun delete(): RequestHeadersUriSpec<*> = mock(RequestHeadersUriSpec::class.java)
    override fun options(): RequestHeadersUriSpec<*> = mock(RequestHeadersUriSpec::class.java)
    override fun method(method: HttpMethod): WebClient.RequestBodyUriSpec =
        mock(WebClient.RequestBodyUriSpec::class.java)

    override fun mutate(): WebClient.Builder = mock(WebClient.Builder::class.java)
}
