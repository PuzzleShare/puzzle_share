package com.puzzle.websocket.chat.service

import com.puzzle.websocket.chat.domain.MainChatMessage
import com.puzzle.websocket.chat.domain.Message
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service("mainChatService")
class MainChatServiceImpl(
    private val template: SimpMessagingTemplate,
) : ChatService {
    override fun send(message: Message) {
        message as MainChatMessage
        template.convertAndSend(MAIN_CHAT_TOPIC, message)
    }
}
