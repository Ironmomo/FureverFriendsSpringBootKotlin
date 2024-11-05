package org.example.fureverfriends.service.userfollowing

import org.example.fureverfriends.model.userfollowing.UserFollowing
import org.example.fureverfriends.model.userfollowing.UserFollowingKey
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.ACCEPTED
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.PENDING
import org.example.fureverfriends.repository.user.UserRepository
import org.example.fureverfriends.repository.userfollowing.UserFollowingRepository
import org.springframework.stereotype.Service

@Service
class UserFollowingService(
    private val userFollowingRepository: UserFollowingRepository,
    private val userRepository: UserRepository
) {
    fun followingRequest(followerId: String, followingId: String) {
        val follower = userRepository.findUserByUsername(followerId)
        checkNotNull(follower) { "User does not exist" }
        val following = userRepository.findUserByUsername(followingId)
        checkNotNull(following) { "User does not exist" }
        val newRelation = UserFollowing(
            id = UserFollowingKey(
                follower = followerId, following = followingId
            ),
            follower = follower,
            following = following,
            status = PENDING
        )
        userFollowingRepository.save(newRelation)
    }

    fun acceptFollowingRequest(followerId: String, followingId: String) {
        val userRelation = userFollowingRepository.findUserFollowingByIdFollowerAndIdFollowingAndStatus(
            followerId = followerId,
            followingId = followingId,
            status = PENDING
        )
        checkNotNull(userRelation) { "There is no pending UserRelation" }
        val updatedUserRelation = userRelation.copy(status = ACCEPTED)
        userFollowingRepository.save(updatedUserRelation)
    }

    fun rejectFollowingRequest(followerId: String, followingId: String) {
        val userRelation = userFollowingRepository.findUserFollowingByIdFollowerAndIdFollowingAndStatus(
            followerId = followerId,
            followingId = followingId,
            status = PENDING
        )
        checkNotNull(userRelation) { "There is no pending UserRelation" }
        userFollowingRepository.delete(userRelation)
    }
}