package de.nicetoapp.kapitalmeisterbackend.authentication

import de.nicetoapp.kapitalmeisterbackend.configuration.GeneralProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig(private val generalProperties: GeneralProperties) {

    @Bean
    fun appTokenFilterRegistration(): FilterRegistrationBean<AppTokenFilter> {
        val registrationBean = FilterRegistrationBean<AppTokenFilter>()
        registrationBean.filter = AppTokenFilter(generalProperties.appToken)
        registrationBean.addUrlPatterns("/api/*")
        registrationBean.order = 1
        return registrationBean
    }
}