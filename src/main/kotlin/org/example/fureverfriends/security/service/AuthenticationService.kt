package org.example.fureverfriends.security.service

import org.example.fureverfriends.security.dto.AuthenticationRequest
import org.example.fureverfriends.security.dto.AuthenticationResponse
import org.example.fureverfriends.security.configs.jwt.JwtProperties
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
) {
    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.username,
                authenticationRequest.password
            )
        )
        val user = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val accessToken = createAccessToken(user)
        return AuthenticationResponse(
            accessToken = accessToken,
        )
    }

    private fun createAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getAccessTokenExpiration()
    )

    private fun getAccessTokenExpiration() =
        Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
}