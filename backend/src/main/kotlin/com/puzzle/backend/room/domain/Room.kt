package com.puzzle.backend.room.domain

import com.puzzle.backend.room.dto.request.PlayerRequest
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "Room", timeToLive = 86400)
data class Room(
    // 방 ID
    @Id
    val roomId: String,
    val roomName: String,
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    // 최대 참가자 수
    val maxPlayers: Int,
    // 현재 참가자 목록
    val redPlayers: MutableList<PlayerRequest> = mutableListOf(),
    // 현재 참가자 목록
    val bluePlayers: MutableList<PlayerRequest> = mutableListOf(),
    var master: Long,
    val masterImage: String,
    val masterName: String,
) {
    fun updateMaster(newMaster: Long) {
        this.master = newMaster
    }
}
