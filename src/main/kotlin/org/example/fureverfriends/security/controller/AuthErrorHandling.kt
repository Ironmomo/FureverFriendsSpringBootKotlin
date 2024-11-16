package org.example.fureverfriends.security.controller

import org.example.fureverfriends.dto.error.ErrorResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class AuthErrorHandling {
    @ExceptionHandler(AuthenticationException::class)
    fun handleConflict(exception: AuthenticationException): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            error = exception.message ?: "Conflict occurred"
        )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }
}