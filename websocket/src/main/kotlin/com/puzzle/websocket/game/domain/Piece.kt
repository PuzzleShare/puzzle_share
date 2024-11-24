package com.puzzle.websocket.game.domain

data class Piece(
    var index: Int,
    var bundleNum: Int,
    var correctIndex: MutableList<Int> = MutableList(4) { -1 },
    var type: IntArray = intArrayOf(),
    var position_x: Double = 0.0,
    var position_y: Double = 0.0,
    var locked: Boolean = false,
) {
    // 자바 호환성을 위한 보조 생성자
    constructor(index: Int) : this(
        index = index,
        bundleNum = index,
        correctIndex = MutableList(4) { -1 },
        type = intArrayOf(),
        position_x = 0.0,
        position_y = 0.0,
        locked = false,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Piece

        return index == other.index
    }

    override fun hashCode(): Int {
        return index
    }
}
