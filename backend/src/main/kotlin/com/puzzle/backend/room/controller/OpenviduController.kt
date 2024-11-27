package com.puzzle.backend.room.controller

import io.openvidu.java.client.ConnectionProperties
import io.openvidu.java.client.OpenVidu
import io.openvidu.java.client.OpenViduHttpException
import io.openvidu.java.client.OpenViduJavaClientException
import io.openvidu.java.client.SessionProperties
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

const val FRONT = "https://puzzle-frontend-five.vercel.app"
const val LOCAL_FRONT = "http://localhost:3000"

@RestController
@CrossOrigin(origins = [FRONT, LOCAL_FRONT])
class OpenviduController {
    @Value("\${openvidu.url}")
    private lateinit var openviduUrl: String

    @Value("\${openvidu.secret}")
    private lateinit var openviduSecret: String

    private lateinit var openvidu: OpenVidu

    @PostConstruct
    fun init() {
        this.openvidu = OpenVidu(openviduUrl, openviduSecret)
    }

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/api/sessions")
    @Throws(OpenViduJavaClientException::class, OpenViduHttpException::class)
    fun initializeSession(
        @RequestBody(required = false) params: Map<String, Any>?,
    ): ResponseEntity<String> {
        println("-----------openvidu")
        val properties = SessionProperties.fromJson(params).build()
        val session = openvidu.createSession(properties)
        println(session.sessionId)
        return ResponseEntity(session.sessionId, HttpStatus.OK)
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PostMapping("/api/sessions/{sessionId}/connections")
    @Throws(OpenViduJavaClientException::class, OpenViduHttpException::class)
    fun createConnection(
        @PathVariable sessionId: String,
        @RequestBody(required = false) params: Map<String, Any>?,
    ): ResponseEntity<String> {
        val session = openvidu.getActiveSession(sessionId)
        return if (session == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            val properties = ConnectionProperties.fromJson(params).build()
            val connection = session.createConnection(properties)
            ResponseEntity(connection.token, HttpStatus.OK)
        }
    }

    @PostMapping("/api/get-token")
    @Throws(OpenViduJavaClientException::class, OpenViduHttpException::class)
    fun getToken(
        @RequestBody(required = false) params: Map<String, Any>?,
    ): ResponseEntity<String> {
        val sessionId: String = params?.get("customSessionId") as? String ?: UUID.randomUUID().toString()
        val customSessionId = mapOf("customSessionId" to sessionId)
        val sessionProperties = SessionProperties.fromJson(customSessionId).build()

        // 세션이 이미 존재하면 가져오고, 없으면 생성
        val session = openvidu.activeSessions.find { it.sessionId == sessionId }
            ?: openvidu.createSession(sessionProperties)

        // 연결 토큰 생성
        val connectionProperties = ConnectionProperties.Builder().build()
        val token = session.createConnection(connectionProperties).token

        return ResponseEntity(token, HttpStatus.OK)
    }
}
