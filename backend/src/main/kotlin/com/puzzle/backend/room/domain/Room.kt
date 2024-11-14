package com.puzzle.backend.room.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "Room", timeToLive = 86400)
data class Room(
    @Id
    val roomId: String, // 방 ID
    val roomName: String, // 방 이름
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    val maxPlayers: Int, // 최대 참가자 수
    val redPlayers: MutableList<Long> = mutableListOf(), // 현재 참가자 목록
    val bluePlayers: MutableList<Long> = mutableListOf(), // 현재 참가자 목록
)
