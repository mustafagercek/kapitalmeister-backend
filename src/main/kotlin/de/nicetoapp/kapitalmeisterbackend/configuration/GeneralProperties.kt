package de.nicetoapp.kapitalmeisterbackend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "general")
class GeneralProperties {
    lateinit var seedFilePath: String
}