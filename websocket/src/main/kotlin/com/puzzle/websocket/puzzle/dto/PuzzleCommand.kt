package com.puzzle.websocket.puzzle.dto

data class PuzzleCommand(
    val roomId: String?,
    val sender: String,
    val message: String,
    val targets: String,
    val position_x: Int,
    val position_y: Int,
)
