package com.puzzle.websocket.puzzle.service

import com.puzzle.backend.common.exception.custom.RoomFullException
import com.puzzle.backend.common.status.PuzzleRoomRole
import com.puzzle.websocket.puzzle.domain.Player
import com.puzzle.websocket.puzzle.domain.PuzzleRoom
import com.puzzle.websocket.puzzle.dto.request.RoomIdRequest
import com.puzzle.websocket.puzzle.repository.PlayerRepository
import com.puzzle.websocket.puzzle.repository.PuzzleRoomRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class PuzzleRoomServiceImpl(
    private val puzzleRoomRepository: PuzzleRoomRepository,
    private val playerRepository: PlayerRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) : PuzzleRoomService {

    fun findById(roomId: String): PuzzleRoom = puzzleRoomRepository.findById(roomId)
        .orElseThrow { IllegalArgumentException("PuzzleRoom not found for ID: $roomId") }

    override fun enterRoom(roomId: RoomIdRequest) {
        val userId = 2L // 현재 사용자 ID (예시)
        val room = findById(roomId.roomId)
        val playerCount = room.redPlayers.size + room.bluePlayers.size

        if (playerCount >= room.maxPlayers) {
            throw RoomFullException("방이 가득 찼습니다.")
        }

        if (room.redPlayers.size <= room.bluePlayers.size) {
            room.redPlayers.add(userId)
        } else {
            room.bluePlayers.add(userId)
        }

        puzzleRoomRepository.save(room)
        playerRepository.save(Player(userId, PuzzleRoomRole.USER, room.roomId))

        // 입장 이벤트 WebSocket 전송
        val entranceMessage = "User $userId has entered the room."
        messagingTemplate.convertAndSend("/topic/room/${roomId.roomId}", mapOf("event" to "enter", "message" to entranceMessage))
    }

    override fun leaveRoom(roomId: RoomIdRequest) {
        val userId = 1L // 현재 사용자 ID (예시)
        val player = playerRepository.findById(userId).orElseThrow()

        val room = findById(roomId.roomId)
        room.redPlayers.remove(userId)
        room.bluePlayers.remove(userId)

        if (room.redPlayers.size + room.bluePlayers.size == 0) {
            playerRepository.deleteById(userId)
            puzzleRoomRepository.delete(room)
            return
        } else if (player.role == PuzzleRoomRole.ROOM_MASTER && room.redPlayers.size > 0) {
            val newMaster = playerRepository.findById(room.redPlayers[0]).orElseThrow()
            newMaster.updateRoomRole(PuzzleRoomRole.ROOM_MASTER)
            playerRepository.save(newMaster)
        } else if (player.role == PuzzleRoomRole.ROOM_MASTER && room.bluePlayers.size > 0) {
            val newMaster = playerRepository.findById(room.bluePlayers[0]).orElseThrow()
            newMaster.updateRoomRole(PuzzleRoomRole.ROOM_MASTER)
            playerRepository.save(newMaster)
        }

        playerRepository.deleteById(userId)
        puzzleRoomRepository.save(room)

        // 퇴장 이벤트 WebSocket 전송
        val leaveMessage = "User $userId has left the room."
        messagingTemplate.convertAndSend("/topic/room/${roomId.roomId}", mapOf("event" to "exit", "message" to leaveMessage))
    }

    override fun moveTeam(roomId: RoomIdRequest) {
        val userId = 1L // 현재 사용자 ID (예시)
        val room = findById(roomId.roomId)

        if (room.redPlayers.contains(userId)) {
            room.redPlayers.remove(userId)
            room.bluePlayers.add(userId)
        } else {
            room.bluePlayers.remove(userId)
            room.redPlayers.add(userId)
        }

        puzzleRoomRepository.save(room)

        // 팀 변경 알림 WebSocket 전송
        val switchMessage = "User $userId switched teams."
        messagingTemplate.convertAndSend("/topic/room/${roomId.roomId}", mapOf("event" to "switch", "message" to switchMessage))
    }

    override fun gameStart(roomId: RoomIdRequest) {
        val userId = 1L
        val player = playerRepository.findById(userId).orElseThrow()
        if (player.role != PuzzleRoomRole.ROOM_MASTER) {
            TODO("방장 아님")
        }
        val gameStartMessage = "The game has started!"
        messagingTemplate.convertAndSend("/topic/room/${roomId.roomId}", mapOf("event" to "start", "message" to gameStartMessage))
    }
}
