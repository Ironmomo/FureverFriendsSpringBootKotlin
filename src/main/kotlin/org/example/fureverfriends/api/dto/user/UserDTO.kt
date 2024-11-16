package org.example.fureverfriends.api.dto.user

import org.example.fureverfriends.api.dto.actions.ActionDTO
import org.example.fureverfriends.api.dto.actions.UserAction

data class UserDTO(
    val username: String,
    val actions: List<ActionDTO<UserAction>> = emptyList()
)