package com.puzzle.backend.room.dto.response

import com.puzzle.backend.room.domain.Room
import java.io.Serializable

data class RoomListResponse(
    val roomId: String,
    // 방 이름
    val roomName: String,
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    // 최대 참가자 수
    val maxPlayers: Int,
    // 현재 참가자 수
    val nowPlayers: Int,
    val masterImage: String,
    val masterName: String,
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
                room.puzzleImage,
                room.puzzlePiece,
                room.maxPlayers,
                nowPlayers,
                room.masterImage,
                room.masterName,
            )
    }
}
