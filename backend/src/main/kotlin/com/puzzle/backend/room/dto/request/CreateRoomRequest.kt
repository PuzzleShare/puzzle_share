package com.puzzle.backend.room.dto.request

import com.puzzle.backend.room.domain.Room
import java.io.Serializable
import java.util.UUID

data class CreateRoomRequest(
    val roomName: String, // 방 이름
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    val maxPlayers: Int,
) : Serializable {
    fun toRoom(): Room =
        Room(
            roomId = UUID.randomUUID().toString(),
            roomName = roomName,
            gameMode = gameMode,
            puzzleImage = puzzleImage,
            puzzlePiece = puzzlePiece,
            maxPlayers = maxPlayers,
        )
}
