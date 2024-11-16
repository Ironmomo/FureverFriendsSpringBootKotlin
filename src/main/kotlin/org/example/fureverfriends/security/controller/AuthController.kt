package org.example.fureverfriends.security.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.example.fureverfriends.api.dto.error.ErrorResponseDTO
import org.example.fureverfriends.security.dto.AuthenticationRequest
import org.example.fureverfriends.security.dto.AuthenticationResponse
import org.example.fureverfriends.security.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "403",
                description = "Ungültige Anmeldedaten",
                content = [Content(schema = Schema(implementation = ErrorResponseDTO::class))]
            )
        ]
    )
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse =
        authenticationService.authentication(authRequest)
}