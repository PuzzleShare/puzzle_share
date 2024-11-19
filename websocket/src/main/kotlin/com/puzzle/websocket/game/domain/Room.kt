package com.puzzle.websocket.game.domain

data class Room(
    var name: String = "",
    var userId: String = "",
    var type: GameType = GameType.BATTLE,
    var roomSize: Int = 0,
    var gameType: String = "",
)
