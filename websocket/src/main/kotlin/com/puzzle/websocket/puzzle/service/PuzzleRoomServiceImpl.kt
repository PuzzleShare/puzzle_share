package com.puzzle.websocket.puzzle.service

import com.puzzle.backend.common.exception.custom.RoomFullException
import com.puzzle.backend.common.status.PuzzleRoomRole
import com.puzzle.websocket.puzzle.domain.Player
import com.puzzle.websocket.puzzle.domain.PuzzleRoom
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

    fun findById(roomId: String): PuzzleRoom = puzzleRoomRepository.findById(roomId).orElseThrow()

    override fun enterRoom(roomId: String) {
        val userId = 2L // 현재 사용자 ID (예시)
        val room = findById(roomId)
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
        messagingTemplate.convertAndSend("/topic/room/$roomId/enter", entranceMessage)
    }

    override fun leaveRoom(roomId: String) {
        val userId = 2L // TODO: 현재 사용자
        val room = findById(roomId)

        room.redPlayers.remove(userId)
        room.bluePlayers.remove(userId)

        puzzleRoomRepository.save(room)
        playerRepository.deleteById(userId)

        // 퇴장 이벤트 WebSocket 전송
        val leaveMessage = "User $userId has left the room."
        messagingTemplate.convertAndSend("/topic/room/$roomId/leave", leaveMessage)
    }

    override fun moveTeam() {
        val userId = 2L // 현재 사용자 ID (예시)
        val roomId = "exampleRoomId" // 예시
        val room = findById(roomId)

        // 현재 팀에서 제거하고 다른 팀으로 이동
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
        messagingTemplate.convertAndSend("/topic/room/$roomId/switch", switchMessage)
    }

    override fun gameStart() {
        val roomId = "exampleRoomId" // 예시
        val gameStartMessage = "The game has started!"

        // 게임 시작 알림 WebSocket 전송
        messagingTemplate.convertAndSend("/topic/room/$roomId/start", gameStartMessage)
    }
}
