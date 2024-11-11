package com.puzzle.websocket.chat.service

import com.puzzle.websocket.chat.domain.MainChatMessage

interface ChatService {
    fun sendMainMessage(mainChatMessage: MainChatMessage)
    fun sendInRoomMessage()
    fun sendInGameMessage()
}