package org.example.fureverfriends.controller.user

import org.example.fureverfriends.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.service.user.UserService
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
class UserController(
    private val userService: UserService
) {

    @PostMapping("/create")
    @ResponseStatus(CREATED)
    fun createUser(@RequestBody userRequest: CreateUserRequestDTO) {
        userService.createUser(userRequest)
    }

    @GetMapping("/search/{searchString}")
    @ResponseStatus(FOUND)
    fun searchUsers(@PathVariable searchString: String): FoundUsersDTO = userService.searchForUser(searchString)
}