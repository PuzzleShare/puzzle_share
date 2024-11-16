package com.puzzle.backend.room.domain

import com.puzzle.backend.common.status.RoomRole
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "Player", timeToLive = 86400)
data class Player(
    @Id
    val playerId: Long,
    val role: RoomRole,
    val roomId: String,
)
