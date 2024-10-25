package org.example.fureverfriends.service.post

import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.dto.post.LatestPostsDTO
import org.example.fureverfriends.repository.post.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    paginationProperties: PaginationProperties
) {
    private val pageSize = paginationProperties.pageSize

    fun findLatestPosts(index: Int): LatestPostsDTO {
        val pageable = PageRequest.of(index, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        val postsPage = postRepository.findAll(pageable)
        return LatestPostsDTO(
            posts = postsPage.content.map { it.mapToDTO() },
            isLastPage = postsPage.isLast
        )
    }
}