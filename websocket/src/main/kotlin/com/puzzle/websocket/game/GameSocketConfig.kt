package com.puzzle.websocket.game

import com.puzzle.websocket.game.service.GameService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class GameSocketConfig(
    private val stompHandler: StompHandler,
    private val gameService: GameService,
//    private val customHandshakeInterceptor: CustomHandshakeInterceptor
) : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/game")
            .setAllowedOriginPatterns("*")
//            .addInterceptors(customHandshakeInterceptor)
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // "app"으로 시작하는 메시지는 message-handling 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/app")
        // "queue" 또는 "topic"으로 시작하는 요청은 구독한 모든 사용자에게 브로드캐스트
        registry.enableSimpleBroker("/queue", "/topic")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompHandler)
    }

    // 클라이언트가 채팅방에 접속하려는 URL이 유효한지 확인
    private fun isValidRoomId(roomId: String): Boolean {
        // 유효한 방 번호인지 확인하는 로직
        return gameService.findById(roomId) != null && gameService.gameRooms.containsKey(roomId)
    }
}
