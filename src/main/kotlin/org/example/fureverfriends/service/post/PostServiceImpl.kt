package org.example.fureverfriends.service.post

import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.dto.post.LatestPostsDTO
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.processor.PostLikeProcessor
import org.example.fureverfriends.repository.post.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val postLikeProcessor: PostLikeProcessor,
    paginationProperties: PaginationProperties
): PostService {
    private val pageSize = paginationProperties.pageSize

    override fun findLatestPosts(currentUsername: String, index: Int): LatestPostsDTO {
        val pageable = PageRequest.of(index, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        val postsPage = postRepository.findPostsByFollowedUsers(currentUsername, ACCEPTED, pageable)
        return LatestPostsDTO(
            posts = postsPage.content.map { it.mapToDTO() },
            isLastPage = postsPage.isLast
        )
    }

    @Transactional
    override fun likePost(currentUsername: String, postId: Long) {
        val post = postRepository.findPostById(postId)
        checkNotNull(post) { "Post not found" }
        postLikeProcessor.updateLike(currentUsername, post.user.username, post)
    }
}