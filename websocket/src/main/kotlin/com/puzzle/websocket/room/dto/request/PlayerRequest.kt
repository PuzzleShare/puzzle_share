package com.puzzle.websocket.room.dto.request

import java.io.Serializable

data class PlayerRequest(
    val playerId: Long,
    val playerImage: String,
    val playerName: String
) : Serializable
