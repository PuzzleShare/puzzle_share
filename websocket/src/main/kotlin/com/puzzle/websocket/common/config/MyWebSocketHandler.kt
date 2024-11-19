package com.puzzle.websocket.common.config

import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.TextMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus

class MyWebSocketHandler : TextWebSocketHandler() {
    private val logger: Logger = LoggerFactory.getLogger(MyWebSocketHandler::class.java)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("WebSocket 연결 성공: ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.info("메시지 수신: ${message.payload}")
        session.sendMessage(TextMessage("응답 메시지: ${message.payload}"))
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        logger.info("WebSocket 연결 종료: ${session.id}, 상태: $closeStatus")
    }
}