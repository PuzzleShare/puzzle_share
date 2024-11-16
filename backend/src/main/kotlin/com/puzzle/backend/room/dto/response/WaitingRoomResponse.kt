package com.puzzle.backend.room.dto.response

import com.puzzle.backend.room.domain.Room
import java.io.Serializable

data class WaitingRoomResponse(
    val roomId: String,
    val roomName: String, // 방 이름
    val gameMode: String,
    val puzzlePiece: Int,
    val maxPlayers: Int, // 최대 참가자 수
    val nowPlayers: Int, // 현재 참가자 수
    val redPlayers: List<Long> = listOf(), // 현재 참가자 목록
    val bluePlayers: List<Long> = listOf(), // 현재 참가자 목록
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
            )
    }
}
