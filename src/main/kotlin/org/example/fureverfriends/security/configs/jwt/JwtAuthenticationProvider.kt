package org.example.fureverfriends.security.configs.jwt

import org.example.fureverfriends.security.service.TokenService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService

class JwtAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = (authentication as JwtAuthentication).getToken()
        val username = tokenService.extractUsername(token)

        if (username != null) {
            val foundUser = userDetailsService.loadUserByUsername(username)

            if (tokenService.isValid(token, foundUser)) {
                return JwtAuthentication.authenticated(username, foundUser.authorities)
            }
        }
        throw RuntimeException("Invalid token")
    }

    override fun supports(authentication: Class<*>): Boolean = authentication == JwtAuthentication::class.java
}