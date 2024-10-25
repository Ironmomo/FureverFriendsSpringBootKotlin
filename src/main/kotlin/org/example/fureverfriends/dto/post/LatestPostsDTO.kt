package org.example.fureverfriends.dto.post

data class LatestPostsDTO(
    val posts: List<PostResponseDTO>,
    val isLastPage: Boolean
)
