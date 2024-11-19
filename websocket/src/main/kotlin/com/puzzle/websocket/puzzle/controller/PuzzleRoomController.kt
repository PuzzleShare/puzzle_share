package com.puzzle.websocket.puzzle.controller

import com.puzzle.websocket.puzzle.dto.request.PlayerRequest
import com.puzzle.websocket.puzzle.dto.request.RoomIdRequest
import com.puzzle.websocket.puzzle.service.PuzzleRoomService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class PuzzleRoomController(
    private val puzzleRoomService: PuzzleRoomService
) {

    @MessageMapping("/room/{roomId}/enter")
    fun entranceRoom(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest
    ) {
        puzzleRoomService.enterRoom(roomId, playerRequest)
    }

    @MessageMapping("/room/{roomId}/exit")
    fun exitRoom(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest
    ) {
        puzzleRoomService.leaveRoom(roomId, playerRequest)
    }

    @MessageMapping("/room/{roomId}/switch")
    fun switchTeam(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest
    ) {
        puzzleRoomService.moveTeam(roomId, playerRequest)
    }

    @MessageMapping("/room/{roomId}/start")
    fun startGame(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest
    ) {
        puzzleRoomService.gameStart(roomId, playerRequest)
    }
}
