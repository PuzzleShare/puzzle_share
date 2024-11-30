package com.puzzle.websocket.room.dto.request

import java.io.Serializable

data class PlayerRequest(
    val playerId: Long? = null,
    val playerImage: String? = null,
    val playerName: String? = null,
) : Serializable
