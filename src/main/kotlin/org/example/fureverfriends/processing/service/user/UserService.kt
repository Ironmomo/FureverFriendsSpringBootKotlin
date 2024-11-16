package org.example.fureverfriends.processing.service.user

import org.example.fureverfriends.api.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.api.dto.user.FoundUsersDTO
import org.example.fureverfriends.model.user.User

interface UserService {

    fun createUser(user: org.example.fureverfriends.api.dto.user.CreateUserRequestDTO)

    fun searchForUser(searchString: String): org.example.fureverfriends.api.dto.user.FoundUsersDTO

    fun getUserByUsername(username: String): User
}