package com.puzzle.backend.common.oauth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "User", timeToLive = 86400)
class UserCache(
    @Id
    val userId: Long,
    val refreshToken: String
)
