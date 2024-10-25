package org.example.fureverfriends.security.service

import org.assertj.core.api.Assertions.assertThat
import org.example.fureverfriends.security.configs.jwt.JwtProperties
import org.example.fureverfriends.stubs.stubAuthenticationRequest
import org.example.fureverfriends.stubs.stubUserDetails
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.security.authentication.AuthenticationManager
import kotlin.test.Test

class AuthenticationServiceTest {
    @Test
    fun `should call services`() {
        val authenticationRequest = stubAuthenticationRequest()
        val userDetail = stubUserDetails()
        val userDetailsService: CustomUserDetailsService = mock {
            on { loadUserByUsername(authenticationRequest.username) } doReturn userDetail
        }
        val tokenService: TokenService = mock {
            on { generate(any(), any(), any()) } doReturn "token"
        }
        val authenticationService = createAuthenticationService(
            userDetailsService = userDetailsService,
            tokenService = tokenService
        )

        val authenticationResponse = authenticationService.authentication(authenticationRequest)

        assertThat(authenticationResponse.accessToken).isEqualTo("token")
    }

    private fun createAuthenticationService(
        authManager: AuthenticationManager = mock(),
        userDetailsService: CustomUserDetailsService = mock(),
        tokenService: TokenService = mock(),
        jwtProperties: JwtProperties = mock()
    ) = AuthenticationService(
        authManager = authManager,
        userDetailsService = userDetailsService,
        tokenService = tokenService,
        jwtProperties = jwtProperties
    )
}