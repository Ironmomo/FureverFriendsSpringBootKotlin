package org.example.fureverfriends.model.userfollowing

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.example.fureverfriends.model.user.User
import org.example.fureverfriends.model.userfollowing.UserRelationStatus.PENDING

@Entity
@Table(name = "user_following")
data class UserFollowing(
    @EmbeddedId
    var id: UserFollowingKey,

    @ManyToOne
    @MapsId("follower")
    @JoinColumn(name = "follower_id")
    val follower: User,

    @ManyToOne
    @MapsId("following")
    @JoinColumn(name = "following_id")
    val following: User,

    @Column
    val status: UserRelationStatus = PENDING
    )

enum class UserRelationStatus {
    ACCEPTED, PENDING
}
