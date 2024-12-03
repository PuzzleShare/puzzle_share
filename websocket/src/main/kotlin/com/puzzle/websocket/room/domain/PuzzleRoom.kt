package com.puzzle.websocket.room.domain

import com.puzzle.websocket.room.dto.request.PlayerRequest
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime

@RedisHash(value = "Room", timeToLive = 86400)
class PuzzleRoom(
    @Id
    val roomId: String, // 방 ID
    val roomName: String, // 방 이름
    val gameMode: String,
    var roomStatus: String,
    val puzzleImage: String,
    var imgWidth: Int = 0,
    var imgHeight: Int = 0,
    val puzzlePiece: Int,
    val maxPlayers: Int, // 최대 참가자 수
    val redPlayers: MutableList<PlayerRequest> = mutableListOf(), // 현재 참가자 목록
    val bluePlayers: MutableList<PlayerRequest> = mutableListOf(), // 현재 참가자 목록
    var master: Long,
    var masterImage: String,
    var masterName: String,
    var battleTimer: Int? = null,
    var createdAt: LocalDateTime,
) {
    fun updateMaster(newMaster: PlayerRequest) {
        this.master = newMaster.playerId!!
        this.masterImage = newMaster.playerImage.toString()
        this.masterName = newMaster.playerName.toString()
    }
}
