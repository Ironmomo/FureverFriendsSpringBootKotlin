package org.example.fureverfriends.security.service

import io.jsonwebtoken.ExpiredJwtException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.example.fureverfriends.security.configs.jwt.JwtProperties
import org.example.fureverfriends.stubs.stubJwtProperties
import org.example.fureverfriends.stubs.stubUserDetails
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue

class TokenServiceTest {

    @Test
    fun `test generate token`() {
        val userDetails = stubUserDetails()
        val expirationDate = Date(System.currentTimeMillis() + 1000 * 60 * 10)
        val tokenService = createTokenService()

        val token = tokenService.generate(userDetails, expirationDate)

        assertAll(
            { assertNotNull(token) },
            { assertEquals("username", tokenService.extractUsername(token)) }
        )
    }

    @Test
    fun `test token is valid`() {
        val userDetails = stubUserDetails()
        val expirationDate = Date(System.currentTimeMillis() + 1000 * 60 * 10)
        val tokenService = createTokenService()

        val token = tokenService.generate(userDetails, expirationDate)

        assertTrue(tokenService.isValid(token, userDetails))
    }

    @Test
    fun `test token is expired`() {
        val userDetails = stubUserDetails()
        val expirationDate = Date(System.currentTimeMillis() - 1000 * 60 * 10)
        val tokenService = createTokenService()

        val token = tokenService.generate(userDetails, expirationDate)
        val throwableIsValid = catchThrowable { tokenService.isValid(token, userDetails) }
        val throwableIsExpired = catchThrowable { tokenService.isExpired(token) }

        assertAll(
            { assertThat(throwableIsExpired).isInstanceOf(ExpiredJwtException::class.java) },
            { assertThat(throwableIsValid).isInstanceOf(ExpiredJwtException::class.java) }
        )
    }

    private fun createTokenService(
        jwtProperties: JwtProperties = stubJwtProperties()
    ) = TokenService(jwtProperties)
}