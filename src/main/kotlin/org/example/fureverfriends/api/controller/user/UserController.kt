package org.example.fureverfriends.api.controller.user

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.fureverfriends.processing.service.user.UserService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.FOUND
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "Operations related to users.")
class UserController(
    private val userService: UserService
) {

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "409",
                description = "User already exists",
                content = [Content(schema = Schema(implementation = org.example.fureverfriends.api.dto.error.ErrorResponseDTO::class))]
            ),
            ApiResponse(
                responseCode = "406",
                description = "Password requirements not met",
                content = [Content(schema = Schema(implementation = org.example.fureverfriends.api.dto.error.ErrorResponseDTO::class))]
            )
        ]
    )
    @PostMapping("/create")
    @ResponseStatus(CREATED)
    fun createUser(@RequestBody userRequest: org.example.fureverfriends.api.dto.user.CreateUserRequestDTO) {
        userService.createUser(userRequest)
    }

    @GetMapping("/search/{searchString}")
    @ResponseStatus(FOUND)
    fun searchUsers(@PathVariable searchString: String): org.example.fureverfriends.api.dto.user.FoundUsersDTO = userService.searchForUser(searchString)
}