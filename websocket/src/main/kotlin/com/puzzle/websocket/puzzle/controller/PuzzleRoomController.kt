package com.puzzle.websocket.puzzle.controller

import com.puzzle.websocket.puzzle.service.PuzzleRoomService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller


@Controller
class PuzzleRoomController(
    private val puzzleRoomService: PuzzleRoomService
) {

    // 방 입장
    @MessageMapping("/room/{roomId}/enter")
    @SendTo("/topic/room/{roomId}/enter")
    fun entranceRoom(@Payload roomId: String): String {
        puzzleRoomService.enterRoom(roomId)
        return "User entered room $roomId"
    }

    // 방 나가기
    @MessageMapping("/room/{roomId}/exit")
    @SendTo("/topic/room/{roomId}/leave")
    fun exitRoom(@Payload roomId: String): String {
        puzzleRoomService.leaveRoom(roomId)
        return "User left room $roomId"
    }

    // 팀 변경
    @MessageMapping("/room/{roomId}/switch")
    @SendTo("/topic/room/{roomId}/switch")
    fun switchTeam(@Payload roomId: String): String {
        puzzleRoomService.moveTeam()
        return "User switched teams in room $roomId"
    }

    // 게임 시작
    @MessageMapping("/room/{roomId}/start")
    @SendTo("/topic/room/{roomId}/start")
    fun startGame(@Payload roomId: String): String {
        puzzleRoomService.gameStart()
        return "Game started in room $roomId"
    }
}