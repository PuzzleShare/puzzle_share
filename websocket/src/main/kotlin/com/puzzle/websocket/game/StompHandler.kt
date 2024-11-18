package com.puzzle.websocket.game

import com.puzzle.websocket.game.service.GameService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor

@Configuration
class StompHandler(private val gameService: GameService) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {

        return message
    }
}
