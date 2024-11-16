package com.puzzle.backend.room.dto.response

import com.puzzle.backend.room.domain.Room
import java.io.Serializable

class RoomListResponse(
    val roomId: String,
    val roomName: String, // 방 이름
    val gameMode: String,
    val puzzlePiece: Int,
    val maxPlayers: Int, // 최대 참가자 수
    val nowPlayers: Int, // 현재 참가자 수
) : Serializable {
    companion object {
        fun toResponse(
            room: Room,
            nowPlayers: Int,
        ): RoomListResponse =
            RoomListResponse(
                room.roomId,
                room.roomName,
                room.gameMode,
                room.puzzlePiece,
                room.maxPlayers,
                nowPlayers,
            )
    }
}
