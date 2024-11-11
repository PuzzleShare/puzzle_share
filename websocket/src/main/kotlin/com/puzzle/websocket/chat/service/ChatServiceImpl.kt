package com.puzzle.websocket.chat.service

import com.puzzle.websocket.chat.domain.MainChatMessage
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

const val PREFIX = "/topic/chat"
const val MAIN_CHAT_TOPIC = "$PREFIX/main"
const val IN_ROOM_CHAT_TOPIC = "$PREFIX/room"
const val IN_GAME_CHAT_TOPIC = "$PREFIX/game"
@Service
class ChatServiceImpl(
    private val template: SimpMessagingTemplate
) : ChatService {
    override fun sendMainMessage(mainChatMessage: MainChatMessage) {
        template.convertAndSend(MAIN_CHAT_TOPIC, mainChatMessage )
    }

    override fun sendInRoomMessage() {

    }

    override fun sendInGameMessage() {
        TODO("Not yet implemented")
    }
}