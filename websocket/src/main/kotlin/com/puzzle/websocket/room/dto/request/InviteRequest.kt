package com.puzzle.websocket.room.dto.request

data class InviteRequest(
    val fromPlayerId : Long,
    val toPlayerId : Long,
    val fromUserName : String,
)
