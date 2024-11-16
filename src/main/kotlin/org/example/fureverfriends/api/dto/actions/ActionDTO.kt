package org.example.fureverfriends.api.dto.actions

data class ActionDTO<T: Action>(
    val uri: String,
    val action: T
)

interface Action

enum class UserAction: Action {
    FOLLOW
}

enum class PostAction: Action {
    LIKE
}