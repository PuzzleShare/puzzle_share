package com.puzzle.websocket.puzzle.controller

import com.puzzle.websocket.puzzle.dto.request.RoomIdRequest
import com.puzzle.websocket.puzzle.service.PuzzleRoomService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class PuzzleRoomController(
    private val puzzleRoomService: PuzzleRoomService
) {

    @MessageMapping("/room/{roomId}/enter")
    fun entranceRoom(roomId: RoomIdRequest) {
        puzzleRoomService.enterRoom(roomId)
    }

    @MessageMapping("/room/{roomId}/exit")
    fun exitRoom(roomId: RoomIdRequest) {
        puzzleRoomService.leaveRoom(roomId)
    }

    @MessageMapping("/room/{roomId}/switch")
    fun switchTeam(roomId: RoomIdRequest) {
        puzzleRoomService.moveTeam(roomId)
    }

    @MessageMapping("/room/{roomId}/start")
    fun startGame(roomId: RoomIdRequest) {
        puzzleRoomService.gameStart(roomId)
    }
}
