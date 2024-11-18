package com.puzzle.websocket.room.controller

import com.puzzle.websocket.room.dto.request.RoomIdRequest
import com.puzzle.websocket.room.service.PuzzleRoomService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class PuzzleRoomController(
    private val puzzleRoomService: PuzzleRoomService
) {

    @MessageMapping("/room/enter")
    fun entranceRoom(roomId: RoomIdRequest) {
        puzzleRoomService.enterRoom(roomId)
    }

    @MessageMapping("/room/exit")
    fun exitRoom(roomId: RoomIdRequest) {
        puzzleRoomService.leaveRoom(roomId)
    }

    @MessageMapping("/room/switch")
    fun switchTeam(roomId: RoomIdRequest) {
        puzzleRoomService.moveTeam(roomId)
    }

    @MessageMapping("/room/start")
    fun startGame(roomId: RoomIdRequest) {
        puzzleRoomService.gameStart(roomId)
    }
}
