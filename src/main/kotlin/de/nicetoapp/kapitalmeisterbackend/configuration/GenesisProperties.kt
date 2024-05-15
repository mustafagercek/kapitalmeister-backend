package de.nicetoapp.kapitalmeisterbackend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "genesis")
class GenesisProperties {
    lateinit var username: String
    lateinit var password: String
}