package de.nicetoapp.kapitalmeisterbackend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        val requestFilter = ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            println("Request: ${clientRequest.method()} ${clientRequest.url()}")
            clientRequest.headers().forEach { name, values ->
                values.forEach { value ->
                    println("$name: $value")
                }
            }
            Mono.just(clientRequest)
        }

        val responseFilter = ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            println("Response: ${clientResponse.statusCode()}")
            clientResponse.headers().asHttpHeaders().forEach { name, values ->
                values.forEach { value ->
                    println("$name: $value")
                }
            }
            Mono.just(clientResponse)
        }

        return WebClient.builder()
            .filter(requestFilter)
            .filter(responseFilter)
            .build()
    }
}
