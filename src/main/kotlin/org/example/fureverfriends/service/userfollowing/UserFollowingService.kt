package org.example.fureverfriends.service.userfollowing

import org.example.fureverfriends.dto.user.FoundUsersDTO

interface UserFollowingService {

    fun followingRequest(followerId: String, followingId: String)

    fun acceptFollowingRequest(followerId: String, followingId: String)

    fun rejectFollowingRequest(followerId: String, followingId: String)

    fun findFollowings(currentUsername: String, index: Int): FoundUsersDTO

    fun findFollowers(currentUsername: String, index: Int): FoundUsersDTO
}