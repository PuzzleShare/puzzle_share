package com.puzzle.backend.oauth.dto.response

import com.fasterxml.jackson.databind.ObjectMapper
import com.puzzle.backend.oauth.domain.Users

data class UserDataResponse(
    val userName: String,
    val image: String,
    val email: String,
    val provider: String,
)

data class LoginSuccessResponse(
    val userId: Long,
    val userName: String,
    val image: String,
    val email: String,
    val provider: String,
) {
    companion object {
        fun of(users: Users): LoginSuccessResponse =
            LoginSuccessResponse(
                userId = users.userId,
                userName = users.userName,
                image = users.userImage,
                email = users.email,
                provider = users.socialType,
            )
    }

    fun toJson(): String = ObjectMapper().writeValueAsString(this)
}

data class RefreshDataResponse(
    val userId: Long,
    val userName: String,
    val image: String,
    val email: String,
    val provider: String,
    val token: String,
)
