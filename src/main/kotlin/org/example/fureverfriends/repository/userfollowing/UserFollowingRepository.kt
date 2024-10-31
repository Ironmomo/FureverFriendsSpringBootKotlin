package org.example.fureverfriends.repository.userfollowing

import org.example.fureverfriends.model.userfollowing.UserFollowing
import org.example.fureverfriends.model.userfollowing.UserFollowingKey
import org.example.fureverfriends.model.userfollowing.UserRelationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserFollowingRepository: JpaRepository<UserFollowing, UserFollowingKey> {

    @EntityGraph(attributePaths = ["follower", "following"])
    fun findUserFollowingsByIdFollowerAndStatus(followerId: String, status: UserRelationStatus, pageable: Pageable): Page<UserFollowing>

    fun findUserFollowingByIdFollowerAndIdFollowingAndStatus(followerId: String, followingId: String, status: UserRelationStatus): UserFollowing?
}