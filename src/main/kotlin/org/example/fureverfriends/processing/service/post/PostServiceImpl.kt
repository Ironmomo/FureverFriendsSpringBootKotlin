package org.example.fureverfriends.processing.service.post

import org.example.fureverfriends.api.dto.actions.ActionDTO
import org.example.fureverfriends.api.dto.actions.PostAction.LIKE
import org.example.fureverfriends.api.dto.post.LatestPostsDTO
import org.example.fureverfriends.api.uriprovider.PostUriProviderImpl
import org.example.fureverfriends.config.post.PaginationProperties
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.processing.processor.PostLikeProcessor
import org.example.fureverfriends.repository.post.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val postLikeProcessor: PostLikeProcessor,
    paginationProperties: PaginationProperties,
    private val postUriProviderImpl: PostUriProviderImpl
): PostService {
    private val pageSize = paginationProperties.pageSize

    override fun findLatestPosts(currentUsername: String, index: Int): LatestPostsDTO {
        val pageable = PageRequest.of(index, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        val postsPage = postRepository.findPostsByFollowedUsers(currentUsername, ACCEPTED, pageable)
        return LatestPostsDTO(
            posts = postsPage.content.map { it.mapToDTO().copy(
                actions = listOf(ActionDTO(
                    uri = postUriProviderImpl.getLikePostUri(),
                    action = LIKE
                ))
            ) }, isLastPage = postsPage.isLast
        )
    }

    @Transactional
    override fun likePost(currentUsername: String, postId: Long) {
        val post = postRepository.findPostById(postId)
        checkNotNull(post) { "Post not found" }
        postLikeProcessor.updateLike(currentUsername, post.user.username, post)
    }
}