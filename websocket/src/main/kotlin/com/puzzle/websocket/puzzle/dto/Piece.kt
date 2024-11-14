package com.puzzle.websocket.puzzle.dto

data class Piece(
    val index: Int,
    var correctTopIndex: Int = -1,
    var correctBottomIndex: Int = -1,
    var correctLeftIndex: Int = -1,
    var correctRightIndex: Int = -1,
    var type: IntArray = IntArray(4) { 0 },
)
