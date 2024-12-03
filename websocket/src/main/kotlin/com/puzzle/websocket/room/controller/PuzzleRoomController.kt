package com.puzzle.websocket.room.controller

import com.puzzle.websocket.room.dto.request.InviteRequest
import com.puzzle.websocket.room.dto.request.PlayerRequest
import com.puzzle.websocket.room.service.PuzzleRoomService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class PuzzleRoomController(
    private val puzzleRoomService: PuzzleRoomService,
) {
    @MessageMapping("/room/{roomId}/enter")
    fun entranceRoom(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest,
    ) {
        puzzleRoomService.enterRoom(roomId, playerRequest)
    }

    @MessageMapping("/room/{roomId}/invite")
    fun invitePlayer(
        @DestinationVariable("roomId") roomId: String,
        inviteRequest : InviteRequest,
    ){
        puzzleRoomService.invitePlayerToRoom(roomId, inviteRequest)
    }

    @MessageMapping("/room/{roomId}/exit")
    fun exitRoom(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest,
    ) {
        puzzleRoomService.leaveRoom(roomId, playerRequest)
    }

    @MessageMapping("/room/{roomId}/switch")
    fun switchTeam(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest,
    ) {
        puzzleRoomService.moveTeam(roomId, playerRequest)
    }
}
