package com.puzzle.websocket.puzzle.dto.request

sealed class PuzzleRequest(
    open val gameId: Long,
)

data class PieceMoveRequest(
    val userName: String,
    val team: String,
    val pieceIndex: Int,
    val x: Int,
    val y: Int,
    override val gameId: Long,
) : PuzzleRequest(gameId)
