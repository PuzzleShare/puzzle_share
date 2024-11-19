package com.puzzle.websocket.puzzle.service

import com.puzzle.backend.common.exception.custom.RoomFullException
import com.puzzle.websocket.common.exception.custom.NoneMasterException
import com.puzzle.websocket.puzzle.dto.request.PlayerRequest
import com.puzzle.websocket.puzzle.domain.PuzzleRoom
import com.puzzle.websocket.puzzle.repository.PuzzleRoomRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class PuzzleRoomServiceImpl(
    private val puzzleRoomRepository: PuzzleRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) : PuzzleRoomService {

    fun findById(roomId: String): PuzzleRoom = puzzleRoomRepository.findById(roomId)
        .orElseThrow { IllegalArgumentException("PuzzleRoom not found for ID: $roomId") }

    override fun enterRoom(roomId: String, playerRequest: PlayerRequest) {
        val room = findById(roomId)
        val playerCount = room.redPlayers.size + room.bluePlayers.size

        if (playerCount >= room.maxPlayers) {
            throw RoomFullException("방이 가득 찼습니다.")
        }

        if (room.redPlayers.size <= room.bluePlayers.size) {
            room.redPlayers.add(playerRequest)
        } else {
            room.bluePlayers.add(playerRequest)
        }

        puzzleRoomRepository.save(room)

        // 입장 이벤트 WebSocket 전송
        val entranceMessage = "User ${playerRequest.playerId} has entered the room."
        print(entranceMessage)
        messagingTemplate.convertAndSend("/topic/room/${roomId}", mapOf("event" to "enter", "message" to entranceMessage, "player" to playerRequest))
    }

    override fun leaveRoom(roomId: String, playerRequest: PlayerRequest) {
        val room = findById(roomId)
        room.redPlayers.remove(playerRequest)
        room.bluePlayers.remove(playerRequest)

        if (room.redPlayers.size + room.bluePlayers.size == 0) {
            puzzleRoomRepository.delete(room)
            return
        } else if (playerRequest.playerId == room.master && room.redPlayers.size > 0) {
            room.updateMaster(room.redPlayers[0].playerId)
        } else if (playerRequest.playerId == room.master && room.bluePlayers.size > 0) {
            room.updateMaster(room.bluePlayers[0].playerId)
        }

        puzzleRoomRepository.save(room)

        // 퇴장 이벤트 WebSocket 전송
        val leaveMessage = "User ${playerRequest.playerId} has left the room."
        print(leaveMessage)
        messagingTemplate.convertAndSend("/topic/room/${roomId}", mapOf("event" to "exit", "message" to leaveMessage, "player" to playerRequest))
    }

    override fun moveTeam(roomId: String, playerRequest: PlayerRequest) {
        val room = findById(roomId)

        if (room.redPlayers.contains(playerRequest)) {
            room.redPlayers.remove(playerRequest)
            room.bluePlayers.add(playerRequest)
        } else {
            room.bluePlayers.remove(playerRequest)
            room.redPlayers.add(playerRequest)
        }

        puzzleRoomRepository.save(room)

        // 팀 변경 알림 WebSocket 전송
        val switchMessage = "User ${playerRequest.playerId} switched teams."
        print(switchMessage)
        messagingTemplate.convertAndSend("/topic/room/${roomId}", mapOf("event" to "switch", "message" to switchMessage, "player" to playerRequest))
    }

    override fun gameStart(roomId: String, playerRequest: PlayerRequest) {
        val room = findById(roomId)
        if (playerRequest.playerId != room.master) {
            throw NoneMasterException()
        }

        val gameStartMessage = "The game has started!"
        print(gameStartMessage)
        messagingTemplate.convertAndSend("/topic/room/${roomId}", mapOf("event" to "start", "message" to gameStartMessage, "player" to playerRequest))
    }
}
