package org.example.fureverfriends.processing.processor

import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.repository.post.PostRepository
import org.example.fureverfriends.util.aop.userfollowing.CheckFollower
import org.springframework.stereotype.Component

@Component
class PostLikeProcessor(
    private val postRepository: PostRepository
) {
    @CheckFollower
    fun updateLike(followerId: String, followingId: String, post: Post) {
        val updatedPost = post.copy(likes = post.likes + 1)
        postRepository.save(updatedPost)
    }
}