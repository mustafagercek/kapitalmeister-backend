package de.nicetoapp.kapitalmeisterbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class KapitalmeisterBackendApplication

fun main(args: Array<String>) {
    runApplication<KapitalmeisterBackendApplication>(*args)
}
