package org.example.fureverfriends.service.user

import org.example.fureverfriends.dto.user.CreateUserRequestDTO
import org.example.fureverfriends.dto.user.FoundUsersDTO
import org.example.fureverfriends.model.user.User

interface UserService {

    fun createUser(user: CreateUserRequestDTO)

    fun searchForUser(searchString: String): FoundUsersDTO

    fun getUserByUsername(username: String): User
}