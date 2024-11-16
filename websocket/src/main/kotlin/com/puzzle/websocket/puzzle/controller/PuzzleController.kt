package com.puzzle.websocket.puzzle.controller

import com.puzzle.websocket.puzzle.service.PuzzleService
import org.springframework.stereotype.Controller

@Controller
class PuzzleController(
    private val puzzleService: PuzzleService,
) {
    fun gameStart() {
    }

    fun movePiece() {
    }

    fun matchPiece() {
    }
}
