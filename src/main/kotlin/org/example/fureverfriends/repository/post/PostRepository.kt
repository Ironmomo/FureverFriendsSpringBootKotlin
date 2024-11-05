package org.example.fureverfriends.repository.post

import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import org.example.fureverfriends.model.post.Post
import org.example.fureverfriends.model.userfollowing.UserRelationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: JpaRepository<Post, Long> {

    @Query(
        """
    SELECT p FROM Post p
    JOIN FETCH p.user u
    JOIN UserFollowing uf ON uf.following.username = p.user.username
    WHERE uf.follower.username = :currentUsername
    AND uf.status = :status
    """,
        countQuery = """
        SELECT COUNT(*) FROM Post p
        JOIN UserFollowing uf ON uf.following.username = p.user.username
        WHERE uf.follower.username = :currentUsername
        AND uf.status = :status
    """
    )
    fun findPostsByFollowedUsers(
        @Param("currentUsername") currentUsername: String,
        @Param("status") status: UserRelationStatus,
        pageable: Pageable
    ): Page<Post>

    @Lock(PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = ["user"])
    fun findPostById(postId: Long): Post?
}