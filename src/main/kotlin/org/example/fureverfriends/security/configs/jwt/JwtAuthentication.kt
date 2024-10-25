package org.example.fureverfriends.security.configs.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JwtAuthentication(
    private val token: String? = null,
    private val username: String? = null,
    private val authorities: Collection<GrantedAuthority> = emptyList()
) : Authentication {

    companion object {
        fun unauthenticated(token: String): JwtAuthentication = JwtAuthentication(token = token)
        fun authenticated(username: String, authorities: Collection<GrantedAuthority>): JwtAuthentication = JwtAuthentication(username = username, authorities = authorities)
    }

    fun getToken(): String = token ?: ""

    override fun getName(): String = username ?: ""

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getCredentials(): Any = ""

    override fun getDetails(): Any = ""

    override fun getPrincipal(): Any = name

    override fun isAuthenticated(): Boolean = username != null

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw IllegalArgumentException("Cannot change authentication state")
    }
}