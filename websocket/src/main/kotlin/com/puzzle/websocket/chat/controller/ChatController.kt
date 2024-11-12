package com.puzzle.websocket.chat.controller

import com.puzzle.websocket.chat.domain.MainChatMessage
import com.puzzle.websocket.chat.service.ChatService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    @Qualifier("mainChatService")
    private val mainChatService: ChatService,
) {
    @MessageMapping("/chat/main")
    fun mainChat(mainChatMessage: MainChatMessage) {
        mainChatService.send(mainChatMessage)
    }
}
