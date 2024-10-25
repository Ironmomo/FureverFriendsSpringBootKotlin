package org.example.fureverfriends.security.configs.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        try {
            val authenticationRequest = JwtAuthentication.unauthenticated(token = jwtToken)
            val authentication = authenticationManager.authenticate(authenticationRequest)
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
            return
            // TODO: Do add Provider to manager
        } catch (e: ExpiredJwtException) {
            handleException(response, "JWT token expired", HttpStatus.UNAUTHORIZED)
        } catch (e: SignatureException) {
            handleException(response, "Invalid JWT signature", HttpStatus.UNAUTHORIZED)
            return
        } catch (e: MalformedJwtException) {
            handleException(response, "Malformed JWT token", HttpStatus.UNAUTHORIZED)
            return
        } catch (e: Exception) {
            handleException(response, "JWT token validation error", HttpStatus.UNAUTHORIZED)
            return
        }
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun handleException(response: HttpServletResponse, message: String, status: HttpStatus) {
        response.status = status.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.writer.write("error: $message")
    }

}