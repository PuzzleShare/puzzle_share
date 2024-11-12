package com.puzzle.websocket.chat.domain

sealed class Message(
    open val message: String,
    open val userName: String,
)

data class InRoomMessage(
    val roomId: Long,
    val roomName: String,
    override val userName: String,
    override val message: String,
) : Message(message, userName)

data class MainChatMessage(
    override val message: String,
    override val userName: String,
) : Message(message, userName)
