package com.puzzle.websocket.game.service

import com.google.gson.Gson
import com.puzzle.websocket.game.domain.Game
import com.puzzle.websocket.game.domain.PieceDto
import com.puzzle.websocket.game.domain.PuzzleBoard
import com.puzzle.websocket.game.domain.ResponseMessage
import com.puzzle.websocket.game.domain.SharePuzzle
import com.puzzle.websocket.room.domain.PuzzleRoom
import org.springframework.stereotype.Service
import java.util.Date

@Service
class GameService {
    val gameRooms: MutableMap<String, Game> = mutableMapOf()
    val gson: Gson = Gson()
    val sessionToGame: MutableMap<String, String> = mutableMapOf()

    // 협동 게임방 불러오기
    fun findAllCooperationRoom(): List<Game> {
        val result = gameRooms.values.filter { it.gameType == "COOPERATION" }.toMutableList()
        result.reverse()
        return result
    }

    // 배틀 게임방 불러오기
    fun findAllBattleRoom(): List<Game> {
        val result = gameRooms.values.filter { it.gameType == "BATTLE" }.toMutableList()
        result.reverse()
        return result
    }

    fun findById(roomId: String): Game? = gameRooms[roomId]

    // 채팅방 생성
    fun createGame(room: PuzzleRoom): Game {
        val game = Game.create(room)
        gameRooms[game.gameId] = game
        println(game.gameId)
        print(game.toString())
        print(gameRooms[game.gameId])
        return game
    }

    fun deleteRoom(name: String) {
        gameRooms.remove(name)
    }

    // 게임 시작
    fun startGame(roomId: String): Game? {
        val game = findById(roomId)
        println("startGame")
        print(game.toString())
        if (game != null) {
            game.start()
        }
        return game
    }

    @Throws(Exception::class)
    fun playGame(sharePuzzle: SharePuzzle): ResponseMessage {
        val roomId = sharePuzzle.roomId
        val sender = sharePuzzle.sender
        var message = sharePuzzle.message
        val targets = sharePuzzle.targets
        val x = sharePuzzle.position_x
        val y = sharePuzzle.position_y

        val res = ResponseMessage()
        val game = findById(roomId) ?: return res.apply { message = "팀 없는데?" }

        val ourPuzzle: PuzzleBoard
        val ourColor: String
        val yourPuzzle: PuzzleBoard
        val yourColor: String

        res.senderId = sender
        if (game.redTeam.map { it.playerId }.contains(sender.toLong())) {
            ourPuzzle = game.redPuzzle!!
            ourColor = "RED"
            yourPuzzle = game.bluePuzzle!!
        } else if (game.blueTeam.map { it.playerId }.contains(sender.toLong())) {
            ourPuzzle = game.bluePuzzle!!
            ourColor = "BLUE"
            yourPuzzle = game.redPuzzle!!
        } else {
            res.message = "팀 없는데?"
            return res
        }

        when (message) {
            "ADD_PIECE" -> {
                val pieces = targets.split(",").mapNotNull { it.toIntOrNull() }
                println("ADD_PIECE")
                ourPuzzle.addPiece(pieces)
                res.team = ourColor
                res.message = "ADD_PIECE"
                res.targets = targets
            }

            "MOUSE_DOWN" -> {
                val arr = gson.fromJson(targets, Array<PieceDto>::class.java)
                for (now in arr) {
                    val p = ourPuzzle.idxToCoordinate[now.index]
                    if (p != null) {
                        if (ourPuzzle.board[p[0]][p[1]].locked) {
                            res.message = "BLOCKED"
                            res.targets = targets
                            res.team = ourColor
                            return res
                        }
                        ourPuzzle.board[p[0]][p[1]].locked = true
                    }
                }
                res.message = "LOCKED"
                res.targets = targets
                res.team = ourColor
            }

            "MOUSE_UP" -> {
                val arr = gson.fromJson(targets, Array<PieceDto>::class.java)
                for (now in arr) {
                    val p = ourPuzzle.idxToCoordinate[now.index]
                    if (p != null) {
                        ourPuzzle.board[p[0]][p[1]].locked = false
                    }
                }
                println("$targets 피스 잠금 해제")
                res.message = "UNLOCKED"
                res.targets = targets
                res.team = ourColor
            }

            "MOUSE_DRAG" -> {
                res.message = "MOVE"
                val arr = gson.fromJson(targets, Array<PieceDto>::class.java)
                for (now in arr) {
                    val p = ourPuzzle.idxToCoordinate[now.index]
                    if (p != null) {
                        ourPuzzle.board[p[0]][p[1]].position_x = now.x
                        ourPuzzle.board[p[0]][p[1]].position_y = now.y
                    }
                }
                res.targets = targets
                res.team = ourColor
            }

            else -> {
                println("구현중인 명령어 : $message")
                println("targets = $targets")
            }
        }

        // 게임 끝났는지 마지막에 확인
        if (!game.isSaved) {
            if (game.gameType == "BATTLE") {
                if (ourPuzzle.isCompleted || yourPuzzle.isCompleted) {
                    game.isFinished = true
                    game.finishTime = Date()
                    res.isFinished = true
//                    save(game)
                    game.isSaved = true
                }
            } else if (game.gameType == "COOPERATION") {
                if (ourPuzzle.isCompleted) {
                    game.isFinished = true
                    game.finishTime = Date()
                    res.isFinished = true
//                    save(game)
                    game.isSaved = true
                }
            }
        }

        // 진행도 추가
        if (game.gameType == "BATTLE") {
            if (ourColor == "RED") {
                res.redProgressPercent = calculateProgress(ourPuzzle)
                res.blueProgressPercent = calculateProgress(yourPuzzle)
            } else {
                res.blueProgressPercent = calculateProgress(ourPuzzle)
                res.redProgressPercent = calculateProgress(yourPuzzle)
            }
        } else {
            res.redProgressPercent = calculateProgress(ourPuzzle)
        }
        return res
    }

    private fun calculateProgress(puzzle: PuzzleBoard): Double =
        (
            puzzle.correctedCount.toDouble() /
                (puzzle.widthCnt.toDouble() * puzzle.lengthCnt.toDouble()) * 100
        )
}
