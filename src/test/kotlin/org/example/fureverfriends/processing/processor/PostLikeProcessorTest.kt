package org.example.fureverfriends.processing.processor

import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.processing.processor.PostLikeProcessor
import org.example.fureverfriends.repository.post.PostRepository
import org.example.fureverfriends.stubs.stubUser
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PostLikeProcessorTest {

    @Nested
    inner class UpdateLikeTests {
        @Test
        fun `should call services`() {
            val currentUserName = "CurrentUser"
            val post = Post(
                title = "Title", content = "Content", user = stubUser()
            )
            val postRepository: PostRepository = mock()
            val processor = createProcessor(postRepository)

            processor.updateLike(currentUserName, post.user.username, post)

            verify(postRepository).save(Post(
                id = post.id,
                title = post.title,
                content = post.content,
                createdAt = post.createdAt,
                likes = post.likes + 1,
                user = post.user
            ))
        }
    }

    private fun createProcessor(
        postRepository: PostRepository = mock()
    ) = PostLikeProcessor(
        postRepository = postRepository
    )
}