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
    val maxPlayers: Int,
    val redPlayers: MutableList<PlayerRequest> = mutableListOf(),
    val bluePlayers: MutableList<PlayerRequest> = mutableListOf(),
    var master: Long,
    var masterImage: String,
    var masterName: String,
) {
    fun updateMaster(newMaster: PlayerRequest) {
        this.master = newMaster.playerId
        this.masterImage = newMaster.playerImage
        this.masterName = newMaster.playerName
    }
}
