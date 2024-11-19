package com.puzzle.websocket.game.domain

data class User(
    val id: String,
    var isMember: Boolean,
    var sessionId: String,
)
