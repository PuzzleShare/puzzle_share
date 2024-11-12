package com.puzzle.websocket.chat.domain

sealed class Message(
    open val message: String,
    open val userName: String,
)

data class InGameMessage(
    val gameId: Long,
    val team: String,
    override val userName: String,
    override val message: String,
) : Message(message, userName)

data class InRoomMessage(
    val roomId: String,
    val roomName: String,
    override val userName: String,
    override val message: String,
) : Message(message, userName)

data class MainChatMessage(
    override val message: String,
    override val userName: String,
) : Message(message, userName)
