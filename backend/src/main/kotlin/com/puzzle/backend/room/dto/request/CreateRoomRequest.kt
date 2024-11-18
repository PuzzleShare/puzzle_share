package com.puzzle.backend.room.dto.request

import com.puzzle.backend.room.domain.Room
import java.io.Serializable
import java.util.UUID

data class CreateRoomRequest(
    val roomName: String,
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    val maxPlayers: Int,
    val playerId: Long,
    val playerImage: String?="imahe",
    val playerName: String?="null"
) : Serializable {
    fun toRoom(playerId: Long): Room =
        Room(
            roomId = UUID.randomUUID().toString(),
            roomName = roomName,
            gameMode = gameMode,
            puzzleImage = puzzleImage,
            puzzlePiece = puzzlePiece,
            maxPlayers = maxPlayers,
            master = playerId
        )
}
