package com.puzzle.backend.room.dto.request

import com.puzzle.backend.room.domain.Room
import java.io.Serializable
import java.util.UUID

data class CreateRoomRequest(
    val roomName: String,
    val gameMode: String,
    val puzzleImage: String,
    val puzzlePiece: Int,
    val width: Int,
    val length: Int,
    val maxPlayers: Int,
    val playerId: Long,
    val playerImage: String,
    val playerName: String,
    val battleTimer: Int? = null,
) : Serializable {
    fun toRoom(): Room =
        Room(
            roomId = UUID.randomUUID().toString(),
            roomName = roomName,
            gameMode = gameMode,
            roomStatus = "WAITING",
            imgWidth = width,
            imgHeight = length,
            puzzleImage = puzzleImage,
            puzzlePiece = puzzlePiece,
            maxPlayers = maxPlayers,
            master = playerId,
            masterImage = playerImage,
            masterName = playerName,
            battleTimer = battleTimer,
        )
}
