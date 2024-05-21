package de.nicetoapp.kapitalmeisterbackend.authentication

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException


class AppTokenFilter(private val appToken: String) : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val token = httpRequest.getHeader("token")

        if (appToken == token) {
            chain.doFilter(request, response)
        } else {
            httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResponse.writer.write("Unauthorized")
        }
    }

    override fun destroy() {}
}
