package com.puzzle.backend.friend.dto.response

import com.puzzle.backend.oauth.domain.Users

data class FriendDataResponse(
    val userId: Long,
    val userName: String,
    val userImage: String,
    val email: String,
) {
    companion object {
        fun from(users: Users): FriendDataResponse =
            FriendDataResponse(
                userId = users.userId,
                userName = users.userName,
                userImage = users.userImage,
                email = users.email,
            )
    }
}

data class CombinedFriendResponse(
    val friends: List<FriendDataResponse>,
    val pendingRequests: List<FriendDataResponse>,
)
