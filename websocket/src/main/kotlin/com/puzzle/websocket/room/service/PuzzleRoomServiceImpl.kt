package com.puzzle.websocket.room.service

import com.puzzle.backend.common.exception.custom.RoomFullException
import com.puzzle.websocket.common.exception.custom.NoneMasterException
import com.puzzle.websocket.game.service.GameService
import com.puzzle.websocket.room.domain.PuzzleRoom
import com.puzzle.websocket.room.dto.request.InviteRequest
import com.puzzle.websocket.room.dto.request.PlayerRequest
import com.puzzle.websocket.room.repository.PuzzleRoomRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class PuzzleRoomServiceImpl(
    private val puzzleRoomRepository: PuzzleRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val gameService: GameService,
) : PuzzleRoomService {
    fun findById(roomId: String): PuzzleRoom =
        puzzleRoomRepository
            .findById(roomId)
            .orElseThrow { IllegalArgumentException("PuzzleRoom not found for ID: $roomId") }

    override fun enterRoom(
        roomId: String,
        playerRequest: PlayerRequest,
    ) {
        val room = findById(roomId)
        if (room.roomStatus=="PLAYING"){
            return
        }
        if (room.bluePlayers.contains(playerRequest) || room.redPlayers.contains(playerRequest)) {
            return
        }
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

        messagingTemplate.convertAndSend("/topic/room/$roomId", room)
    }

    override fun leaveRoom(
        roomId: String,
        playerRequest: PlayerRequest,
    ) {
        val room = findById(roomId)
        room.redPlayers.remove(playerRequest)
        room.bluePlayers.remove(playerRequest)

        if (room.redPlayers.size + room.bluePlayers.size == 0) {
            puzzleRoomRepository.delete(room)
            return
        } else if (playerRequest.playerId == room.master && room.redPlayers.size > 0) {
            room.updateMaster(room.redPlayers[0])
        } else if (playerRequest.playerId == room.master && room.bluePlayers.size > 0) {
            room.updateMaster(room.bluePlayers[0])
        }

        puzzleRoomRepository.save(room)

        messagingTemplate.convertAndSend("/topic/room/$roomId", room)
    }

    override fun moveTeam(
        roomId: String,
        playerRequest: PlayerRequest,
    ) {
        val room = findById(roomId)

        if (room.redPlayers.contains(playerRequest)) {
            if (room.bluePlayers.size == room.maxPlayers / 2) {
                return
            }
            room.redPlayers.remove(playerRequest)
            room.bluePlayers.add(playerRequest)
        } else {
            if (room.redPlayers.size == room.maxPlayers / 2) {
                return
            }
            room.bluePlayers.remove(playerRequest)
            room.redPlayers.add(playerRequest)
        }

        puzzleRoomRepository.save(room)

        messagingTemplate.convertAndSend("/topic/room/$roomId", room)
    }

    override fun gameStart(
        roomId: String,
        playerRequest: PlayerRequest,
    ) {
        val room = findById(roomId)
        if (room.master != playerRequest.playerId) {
            throw NoneMasterException("방장이 아닙니다.")
        }
        var game = gameService.createGame(room)
        game = gameService.startGame(game.gameId)!!

        // 방 상태 게임중으로 변경
        room.roomStatus = "PLAYING"
        puzzleRoomRepository.save(room)
        messagingTemplate.convertAndSend(
            "/topic/room/$roomId/game",
            game,
        )
    }

    override fun invitePlayerToRoom(
        roomId: String,
        inviteRequest: InviteRequest,
    ) {
        val inviteMessage =
            mapOf(
                "type" to "invite",
                "roomId" to roomId,
                "fromPlayerId" to inviteRequest.fromPlayerId,
                "toPlayerId" to inviteRequest.toPlayerId,
                "fromUserName" to inviteRequest.fromUserName,
            )

        messagingTemplate.convertAndSend(
            "/topic/invite/${inviteRequest.toPlayerId}",
            inviteMessage,
        )
    }
}
