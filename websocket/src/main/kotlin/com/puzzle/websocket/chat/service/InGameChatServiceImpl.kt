package com.puzzle.websocket.chat.service

import com.puzzle.websocket.chat.domain.InGameMessage
import com.puzzle.websocket.chat.domain.Message
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service("inGameChatService")
class InGameChatServiceImpl(
    private val template: SimpMessagingTemplate,
) : ChatService {
    override fun send(message: Message) {
        message as InGameMessage
        template.convertAndSend("$IN_GAME_CHAT_TOPIC/${message.gameId}/${message.team}", message)
    }
}
