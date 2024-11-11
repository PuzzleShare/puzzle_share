package com.puzzle.websocket.chat.controller

import com.puzzle.websocket.chat.domain.MainChatMessage
import com.puzzle.websocket.chat.service.ChatService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatController (
    private val chatService: ChatService
){
    @MessageMapping("/chat/main")
    fun mainChat(mainChatMessage: MainChatMessage){
        println("/chat/main 들어옴")
        chatService.sendMainMessage(mainChatMessage)
    }
}