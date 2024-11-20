package com.puzzle.websocket.chat.domain

sealed class Message(
    open val message: String,
    open val userName: String,
    open val userId: Long,
)

data class InGameMessage(
    val gameId: String,
    val team: String,
    override val userName: String,
    override val message: String,
    override val userId: Long
) : Message(message, userName, userId)

data class InRoomMessage(
    val roomId: String,
    val roomName: String,
    override val userName: String,
    override val message: String,
    override val userId: Long,
) : Message(message, userName, userId)

data class MainChatMessage(
    override val message: String,
    override val userName: String,
    override val userId: Long,
) : Message(message, userName, userId)
