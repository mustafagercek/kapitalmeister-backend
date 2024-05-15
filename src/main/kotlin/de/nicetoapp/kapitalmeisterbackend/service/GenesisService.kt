package de.nicetoapp.kapitalmeisterbackend.service

import de.nicetoapp.kapitalmeisterbackend.configuration.GenesisProperties
import de.nicetoapp.kapitalmeisterbackend.model.dto.GenesisTableResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GenesisService(
    private val webClient: WebClient,
    private val genesisProperties: GenesisProperties
) {

    fun fetchTable(tableName: String, additionalParams: Map<String, String> = emptyMap()): Flow<GenesisTableResponse> {
        return webClient.get()
            .uri { builder ->
                builder.scheme(SCHEME)
                    .host(HOST)
                    .path(DATASOURCE_PATH)
                    .queryParam(USERNAME_PARAM, genesisProperties.username)
                    .queryParam(PASSWORD_PARAM, genesisProperties.password)
                    .queryParam(TABLE_NAME_PARAM, tableName)
                additionalParams.forEach { (key, value) ->
                    builder.queryParam(key, value)
                }
                builder.build()
            }
            .retrieve()
            .bodyToFlux(GenesisTableResponse::class.java)
            .asFlow()
    }

    companion object {
        private const val SCHEME = "https"
        private const val HOST = "www-genesis.destatis.de"
        private const val DATASOURCE_PATH = "/genesisWS/rest/2020/data/table"
        private const val USERNAME_PARAM = "username"
        private const val PASSWORD_PARAM = "password"
        private const val TABLE_NAME_PARAM = "name"
    }

}


