package com.puzzle.websocket.game.domain

import com.puzzle.websocket.room.domain.PuzzleRoom
import com.puzzle.websocket.room.dto.request.PlayerRequest
import java.util.Date
import java.util.UUID

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
    fun start() {
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
    }

    fun getTime(): Long {
        val nowTime = Date()
        return (nowTime.time - (startTime?.time ?: 0)) / 1000
    }

    companion object {
        fun create(room: PuzzleRoom): Game {
            val name = room.roomName
            val roomSize = room.maxPlayers
            val gameType = "BATTLE"
            val puzzleImage = room.puzzleImage

            val uuid = UUID.randomUUID().toString()

            // PuzzleRoom에서 받은 puzzleImage 값을 Picture에 전달
            val picture =
                Picture.create(
                    pieceSize = 40, // 적절한 퍼즐 조각 크기를 설정하세요.
                    imageName = puzzleImage,
                    encodedString = puzzleImage,
                )

            val game =
                Game(
                    gameId = uuid,
                    gameName = name,
                    roomSize = roomSize,
                    gameType = gameType,
                    sessionToUser = mutableMapOf(),
                    picture = picture, // 생성한 Picture 객체를 설정
                )

            if (gameType == "BATTLE") {
                game.redTeam = room.redPlayers
                game.blueTeam = room.bluePlayers
                game.startTime = Date()
            } else if (gameType == "COOPERATION") {
                game.redTeam = room.redPlayers
                game.blueTeam = room.bluePlayers
                game.startTime = Date()
            }

            return game
        }
    }
}
