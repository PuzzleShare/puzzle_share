package com.puzzle.websocket.game.domain

data class Piece(
    var index: Int,
    var correctTopIndex: Int = 0,
    var correctBottomIndex: Int = 0,
    var correctLeftIndex: Int = 0,
    var correctRightIndex: Int = 0,
    var type: IntArray = intArrayOf(),
    var position_x: Double = 0.0,
    var position_y: Double = 0.0,
    var locked: Boolean = false,
){
    // 자바 호환성을 위한 보조 생성자
    constructor(index: Int) : this(
        index = index,
        correctTopIndex = 0,
        correctBottomIndex = 0,
        correctLeftIndex = 0,
        correctRightIndex = 0,
        type = intArrayOf(),
        position_x = 0.0,
        position_y = 0.0,
        locked = false
    )
}