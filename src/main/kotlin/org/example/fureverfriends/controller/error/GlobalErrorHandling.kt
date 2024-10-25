package org.example.fureverfriends.controller.error

import org.example.fureverfriends.dto.error.ErrorResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalErrorHandling {

    @ExceptionHandler(IllegalStateException::class )
    fun handleConflict(exception: IllegalStateException): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            error = exception.message ?: "Conflict occurred"
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(IllegalArgumentException::class )
    fun handleConflict(exception: IllegalArgumentException): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            error = exception.message ?: "Conflict occurred"
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_ACCEPTABLE)
    }
}