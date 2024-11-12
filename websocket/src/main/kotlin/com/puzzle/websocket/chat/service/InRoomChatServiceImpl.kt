package com.puzzle.websocket.chat.service

import com.puzzle.websocket.chat.domain.InRoomMessage
import com.puzzle.websocket.chat.domain.Message
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service("inRoomChatService")
class InRoomChatServiceImpl(
    private val template: SimpMessagingTemplate,
) : ChatService {
    override fun send(message: Message) {
        message as InRoomMessage
        template.convertAndSend("$IN_ROOM_CHAT_TOPIC/${message.roomId}", message)
    }
}
