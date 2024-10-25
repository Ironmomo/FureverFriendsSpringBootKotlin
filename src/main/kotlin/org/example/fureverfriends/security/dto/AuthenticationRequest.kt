package org.example.fureverfriends.security.dto

data class AuthenticationRequest(
    val username: String,
    val password: String,
)
