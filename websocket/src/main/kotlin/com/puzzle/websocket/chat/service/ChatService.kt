package com.puzzle.websocket.chat.service

import com.puzzle.websocket.chat.domain.MainChatMessage

const val PREFIX = "/topic/chat"
const val MAIN_CHAT_TOPIC = "$PREFIX/main"
const val IN_ROOM_CHAT_TOPIC = "$PREFIX/room"
const val IN_GAME_CHAT_TOPIC = "$PREFIX/game"

interface ChatService {
    fun send(message: MainChatMessage)
}
