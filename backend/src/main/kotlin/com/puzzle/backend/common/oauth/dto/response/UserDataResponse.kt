package com.puzzle.backend.common.oauth.dto.response

data class UserDataResponse(
    val userName: String,
    val image: String,
    val email: String,
    val provider: String,
)