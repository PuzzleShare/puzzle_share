package com.puzzle.websocket.game.domain

import com.puzzle.websocket.room.domain.PuzzleRoom
import com.puzzle.websocket.room.dto.request.PlayerRequest
import java.util.*

data class Game(
    var gameId: String = "",
    var gameName: String = "",
    var roomSize: Int = 0,
    var gameType: String = "",
    var admin: User? = null,
    var picture: Picture? = null,
    var redTeam: MutableList<PlayerRequest> = mutableListOf(),
    var blueTeam: MutableList<PlayerRequest> = mutableListOf(),
    var players: MutableList<User> = mutableListOf(),
    var redPuzzle: PuzzleBoard? = null,
    var bluePuzzle: PuzzleBoard? = null,
    var startTime: Date? = null,
    var finishTime: Date? = null,
    var isStarted: Boolean = false,
    var sessionToUser: MutableMap<String, User> = mutableMapOf(),
    var isFinished: Boolean = false,
    var isSaved: Boolean = false,
) {
    fun changeTeam(
        a: User?,
        b: User?,
    ) {
//        when {
//            a != null && redTeam.isIn(a) && b != null && blueTeam.isIn(b) -> {
//                redTeam.deletePlayer(a)
//                blueTeam.addPlayer(a)
//                blueTeam.deletePlayer(b)
//                redTeam.addPlayer(b)
//            }
//            a == null && b != null -> {
//                if (redTeam.isIn(b)) {
//                    redTeam.deletePlayer(b)
//                    blueTeam.addPlayer(b)
//                } else if (blueTeam.isIn(b)) {
//                    blueTeam.deletePlayer(b)
//                    redTeam.addPlayer(b)
//                }
//            }
//            b == null && a != null -> {
//                if (redTeam.isIn(a)) {
//                    redTeam.deletePlayer(a)
//                    blueTeam.addPlayer(a)
//                } else if (blueTeam.isIn(a)) {
//                    blueTeam.deletePlayer(a)
//                    redTeam.addPlayer(a)
//                }
//            }
//        }
    }

    fun exitPlayer(sessionId: String) {
        val user: User? = sessionToUser[sessionId]
        user?.let {
            if (it == admin) {
                val values = sessionToUser.values
                if (values.size > 1) {
                    admin = values.elementAt(1)
                    println("방장이 ${admin?.id} 님으로 바뀌었습니다~")
                }
            }

//            if (redTeam.players.contains(it)) {
//                redTeam.deletePlayer(it)
//            } else {
//                blueTeam.deletePlayer(it)
//            }
            sessionToUser.remove(sessionId)
        }
    }

    fun enterPlayer(
        user: User,
        sessionId: String,
    ): Boolean {
        return true
//        if (sessionToUser.isEmpty()) {
//            admin = user
//        }
//        sessionToUser[sessionId] = user
//
//        return if (gameType == "BATTLE") {
//            if (redTeam.players.contains(user) || blueTeam.players.contains(user)) {
//                true
//            } else if (redTeam.players.size < roomSize / 2) {
//                redTeam.addPlayer(user)
//                true
//            } else if (blueTeam.players.size < roomSize / 2) {
//                blueTeam.addPlayer(user)
//                true
//            } else {
//                false
//            }
//        } else {
//            if (redTeam.players.contains(user)) {
//                true
//            } else if (redTeam.players.size < roomSize) {
//                redTeam.addPlayer(user)
//                true
//            } else {
//                false
//            }
//        }
    }

//    fun isEmpty(): Boolean = (redTeam.players.size + blueTeam.players.size) == 0

    fun start() {
//        if (isStarted) return

        redPuzzle = PuzzleBoard()
        bluePuzzle = PuzzleBoard()
        redPuzzle?.init(picture!!, gameType)
        bluePuzzle?.init(picture!!, gameType)
        players =
            mutableListOf<User>().apply {
//                addAll(redTeam.players)
//                addAll(blueTeam.players)
            }
        startTime = Date()
        isStarted = true
        println("------------------게임 시작-------------------")
    }

    fun getTime(): Long {
        val nowTime = Date()
        return (nowTime.time - (startTime?.time ?: 0)) / 1000
    }

    fun updatePicture(p: Picture) {
        picture = p
    }

    companion object {
        fun create(room: PuzzleRoom): Game {
            val name = room.roomName
            val roomSize = room.maxPlayers
            val gameType = "BATTLE"
            val uuid = UUID.randomUUID().toString()

            val game =
                Game(
                    gameId = uuid,
                    gameName = name,
                    roomSize = roomSize,
                    gameType = gameType,
                    sessionToUser = mutableMapOf(),
                )

            if (gameType == "BATTLE") {
                game.redTeam = room.redPlayers

                game.blueTeam = room.bluePlayers
                game.picture = Picture.create()
                game.startTime = Date()
                println("$name 배틀 방 생성 / id = $uuid")
            } else if (gameType == "COOPERATION") {
                game.redTeam = room.redPlayers
                game.blueTeam = room.bluePlayers
                game.picture = Picture.create()
                game.startTime = Date()
                println("$name 협동 방 생성 / id = $uuid")
            }

            return game
        }
    }
}
