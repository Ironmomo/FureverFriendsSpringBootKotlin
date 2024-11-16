package org.example.fureverfriends.api.dto.user

data class FoundUsersDTO(
    val foundUsers: List<UserDTO>,
    val isLastPage: Boolean = true
)
