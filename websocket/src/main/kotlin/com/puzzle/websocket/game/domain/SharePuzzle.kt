package com.puzzle.websocket.game.domain

data class SharePuzzle(
    var roomId: String = "",
    var sender: String = "",
    var message: String = "",
    var targets: String = "",

    var position_x: Int = 0,
    var position_y: Int = 0,

    )
