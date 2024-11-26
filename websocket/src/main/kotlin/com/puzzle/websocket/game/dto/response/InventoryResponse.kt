package com.puzzle.websocket.game.dto.response

class InventoryResponse(
    val team: String,
    val inventory: Array<Int>,
    val fitPieceIndex: Int,
)