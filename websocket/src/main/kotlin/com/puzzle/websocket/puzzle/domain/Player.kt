package com.puzzle.websocket.puzzle.domain

import com.puzzle.backend.common.status.PuzzleRoomRole
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "Player", timeToLive = 86400)
data class Player(
    @Id
    val playerId: Long,
    val role: PuzzleRoomRole,
    val roomId: String,
)