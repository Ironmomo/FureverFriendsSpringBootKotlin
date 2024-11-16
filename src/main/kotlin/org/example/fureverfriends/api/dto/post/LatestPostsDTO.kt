package org.example.fureverfriends.api.dto.post

data class LatestPostsDTO(
    val posts: List<PostResponseDTO>,
    val isLastPage: Boolean
)
