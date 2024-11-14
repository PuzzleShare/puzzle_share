package com.puzzle.websocket.puzzle.controller

import com.puzzle.websocket.puzzle.dto.PuzzleCommand
import com.puzzle.websocket.puzzle.dto.ResponseMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class PuzzleController(
    private val sendingOperations: SimpMessageSendingOperations,
) {
    fun gameStart() {
    }

    @MessageMapping("/puzzle/move")
    fun movePiece(puzzleCommand: PuzzleCommand) {
        println(puzzleCommand.message)
        println("${puzzleCommand.position_x} x 좌표 ${puzzleCommand.position_y} y 좌표")
        val responseMessage = ResponseMessage(message = puzzleCommand.message, x = puzzleCommand.position_x, y = puzzleCommand.position_y)
        sendingOperations.convertAndSend("/topic/puzzle/move", responseMessage)
    }

    fun matchPiece() {
    }
}
