package org.example.fureverfriends.dto.user

data class FoundUsersDTO(
    val foundUsers: List<UserDTO>,
    val isLastPage: Boolean = true
)
