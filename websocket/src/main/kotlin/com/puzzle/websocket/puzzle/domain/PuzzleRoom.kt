package com.puzzle.websocket.puzzle.domain

import com.puzzle.websocket.puzzle.dto.request.PlayerRequest
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "Room", timeToLive = 86400)
class PuzzleRoom(
    @Id
    val roomId: String, // 방 ID
    val roomName: String, // 방 이름
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    val maxPlayers: Int, // 최대 참가자 수
    val redPlayers: MutableList<PlayerRequest> = mutableListOf(), // 현재 참가자 목록
    val bluePlayers: MutableList<PlayerRequest> = mutableListOf(), // 현재 참가자 목록
    var master: Long
) {
    fun updateMaster(newMaster: Long) {
        this.master = newMaster
    }
}
