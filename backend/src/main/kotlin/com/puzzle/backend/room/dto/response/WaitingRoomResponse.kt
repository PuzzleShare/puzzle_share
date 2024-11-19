package com.puzzle.backend.room.dto.response

import com.puzzle.backend.room.domain.Room
import com.puzzle.backend.room.dto.request.PlayerRequest
import java.io.Serializable

data class WaitingRoomResponse(
    val roomId: String,
    // 방 이름
    val roomName: String,
    val gameMode: String,
    val puzzlePiece: Int,
    // 최대 참가자 수
    val maxPlayers: Int,
    // 현재 참가자 수
    val nowPlayers: Int,
    // 현재 참가자 목록
    val redPlayers: List<PlayerRequest> = listOf(),
    // 현재 참가자 목록
    val bluePlayers: List<PlayerRequest> = listOf(),
    val master: Long
) : Serializable {
    companion object {
        fun toResponse(
            room: Room,
            nowPlayers: Int,
        ): WaitingRoomResponse =
            WaitingRoomResponse(
                room.roomId,
                room.roomName,
                room.gameMode,
                room.puzzlePiece,
                room.maxPlayers,
                nowPlayers,
                room.redPlayers,
                room.bluePlayers,
                room.master
            )
    }
}
