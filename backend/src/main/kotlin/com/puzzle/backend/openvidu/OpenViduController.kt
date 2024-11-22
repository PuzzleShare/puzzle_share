package com.puzzle.websocket.chat.openvidu

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

@RestController
@CrossOrigin(origins = ["*"])
class OpenViduController {
    @Value("\${openvidu.url}")
    private val openviduUrl: String? = null

    @Value("\${openvidu.secret}")
    private val openviduSecret: String? = null

    private var openvidu: OpenVidu? = null

    @PostConstruct
    fun init() {
        this.openvidu = OpenVidu(openviduUrl, openviduSecret)
    }

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/api/sessions")
    @Throws(
        OpenViduJavaClientException::class,
        OpenViduHttpException::class,
    )
    fun initializeSession(
        @RequestBody(required = false) params: Map<String?, Any?>?,
    ): ResponseEntity<String> {
        val properties = SessionProperties.fromJson(params).build()
        val session = openvidu!!.createSession(properties)
        return ResponseEntity(session.sessionId, HttpStatus.OK)
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PostMapping("/api/sessions/{sessionId}/connections")
    @Throws(
        OpenViduJavaClientException::class,
        OpenViduHttpException::class,
    )
    fun createConnection(
        @PathVariable("sessionId") sessionId: String?,
        @RequestBody(required = false) params: Map<String?, Any?>?,
    ): ResponseEntity<String> {
        val session =
            openvidu!!.getActiveSession(sessionId)
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        println("openvidu ${session.sessionId}")
        val properties = ConnectionProperties.fromJson(params).build()
        val connection = session.createConnection(properties)
        return ResponseEntity(connection.token, HttpStatus.OK)
    }
}
