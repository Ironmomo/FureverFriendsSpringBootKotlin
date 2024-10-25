package org.example.fureverfriends.repository.post

import org.example.fureverfriends.model.post.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = ["user"])
    override fun findAll(pageable: Pageable): Page<Post>
}