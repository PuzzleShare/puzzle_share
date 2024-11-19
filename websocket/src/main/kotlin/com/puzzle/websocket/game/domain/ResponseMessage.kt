package com.puzzle.websocket.game.domain


data class ResponseMessage(
    var game: Game? = null,
    var senderId: String? = null,
    var message: String? = null,
    var team: String? = null,
    var targetList: List<Int>? = null,
    var positionX: Int = 0,
    var positionY: Int = 0,
    var targets: String? = null,
    var isFinished: Boolean = false,
    var redProgressPercent: Double = -1.0,
    var blueProgressPercent: Double = -1.0,
    var deleted: Map<Int, DoubleArray>? = null,
    var redBundles: List<Set<Piece>>? = null,
    var blueBundles: List<Set<Piece>>? = null

)
