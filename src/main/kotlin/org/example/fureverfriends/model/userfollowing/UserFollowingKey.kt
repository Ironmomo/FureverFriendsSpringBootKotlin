package org.example.fureverfriends.model.userfollowing

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserFollowingKey(
    @Column(name = "follower_id")
    val follower: String,
    @Column(name = "following_id")
    val following: String
)
