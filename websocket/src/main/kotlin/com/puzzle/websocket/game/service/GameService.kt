package com.puzzle.websocket.game.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import com.puzzle.websocket.game.domain.Game
import com.puzzle.websocket.game.domain.ListStringUtils
import com.puzzle.websocket.game.domain.Picture
import com.puzzle.websocket.game.domain.Piece
import com.puzzle.websocket.game.domain.PieceDto
import com.puzzle.websocket.game.domain.PuzzleBoard
import com.puzzle.websocket.game.domain.ResponseMessage
import com.puzzle.websocket.game.domain.SharePuzzle
import com.puzzle.websocket.game.domain.User
import com.puzzle.websocket.game.dto.response.InventoryResponse
import com.puzzle.websocket.game.enums.GameItem
import com.puzzle.websocket.room.domain.PuzzleRoom
import com.puzzle.websocket.room.dto.request.PlayerRequest
import com.puzzle.websocket.room.repository.PuzzleRoomRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import java.util.Date
import java.util.concurrent.locks.ReentrantLock

@Service
class GameService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val sendingOperations: SimpMessageSendingOperations,
    private val puzzleRoomRepository: PuzzleRoomRepository,
) {
    val gameRooms: MutableMap<String, Game> = mutableMapOf()
    val gson: Gson = Gson()
    val sessionToGame: MutableMap<String, String> = mutableMapOf()
    private val lock = ReentrantLock()

    val objectMapper = jacksonObjectMapper()
    val jsonMapper: ObjectMapper =
        ObjectMapper()
            .registerKotlinModule()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun deleteGame(gameId: String) {
        val basicKey = "$gameKeyPrefix$gameId"
//        gameRooms.remove(gameId)
        val keysToDelete =
            listOf(
                "$basicKey:meta",
                "$basicKey:redTeam",
                "$basicKey:blueTeam",
                "$basicKey:players",
                "$basicKey:picture",
                "$basicKey:admin",
                "$basicKey:redPuzzle:correctedCount",
                "$basicKey:redPuzzle:isCompleted",
                "$basicKey:redPuzzle:idxToCoordinate",
                "$basicKey:redPuzzle:board",
                "$basicKey:redPuzzle:isCorrected",
                "$basicKey:bluePuzzle:correctedCount",
                "$basicKey:bluePuzzle:isCompleted",
                "$basicKey:bluePuzzle:idxToCoordinate",
                "$basicKey:bluePuzzle:board",
                "$basicKey:bluePuzzle:isCorrected",
            )
        redisTemplate.delete(keysToDelete)
    }

    fun deleteGameRoom(gameId: String) {
        gameRooms.remove(gameId)
    }

    // 배틀 게임방 불러오기
    fun findAllBattleRoom(): List<Game> {
        val result = gameRooms.values.filter { it.gameType == "BATTLE" }.toMutableList()
        result.reverse()
        return result
    }

    fun findById(roomId: String): Game? {
        if (gameRooms[roomId] == null) {
            val game = load(roomId)
            gameRooms[roomId] = game
        }
        return gameRooms[roomId]
    }

    fun createGame(room: PuzzleRoom): Game {
        val game = Game.create(room)
        gameRooms[game.gameId] = game

        return game
    }

    fun startGame(roomId: String): Game? {
        val game = findById(roomId)
        if (game != null) {
            game.start()
            save(game)
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
                lock.lock() // 락 획득
                try {
                    val pieces = targets.split(",").mapNotNull { it.toIntOrNull() }
                    ourPuzzle.addPiece(pieces)
                    pieces.forEach {
                        if (ourPuzzle.itemPiece.contains(it) && !ourPuzzle.itemPiece[it]!!) {
                            val attackItem = listOf(1, 2, 3, 4)
                            ourPuzzle.addItem(attackItem[(Math.random() * attackItem.size).toInt()])
                            ourPuzzle.itemPiece[it] = true
                            sendingOperations.convertAndSend(
                                "/topic/game/room/$roomId/useItem",
                                InventoryResponse(
                                    team = ourColor,
                                    inventory = ourPuzzle.inventory,
                                    fitPieceIndex = it,
                                ),
                            )
                        }
                    }

                    res.team = ourColor
                    res.message = "ADD_PIECE"
                    res.targets = targets
                } finally {
                    lock.unlock() // 반드시 락 해제
                }

                savePuzzle(game)
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
                res.message = "UNLOCKED"
                res.targets = targets
                res.team = ourColor
            }

            "MOUSE_DRAG" -> {
                res.message = "MOVE"
                res.targets = targets
                res.team = ourColor
                sendingOperations.convertAndSend("/topic/game/room/${sharePuzzle.roomId}", res)
                val arr = gson.fromJson(targets, Array<PieceDto>::class.java)
                val (row, col) = ourPuzzle.idxToCoordinate[arr[0].index]!!
                val piece = ourPuzzle.board[row][col]
                piece.position_x = arr[0].x
                piece.position_y = arr[0].y

                ourPuzzle.bundles[piece.bundleNum]!!.forEach {
                    if (it != piece)
                        {
                            val (x, y) = getNewPoint(ourPuzzle, piece, it)
                            it.position_x = x
                            it.position_y = y
                        }
                }
            }

            "USE_ITEM" -> {
                val slotNum = targets.toIntOrNull()
                if (slotNum != null && slotNum >= 0 && slotNum < ourPuzzle.inventory.size) {
                    val itemIdx = ourPuzzle.inventory[slotNum]
                    if (itemIdx > 0 && itemIdx <= GameItem.values().size) {
                        val gameItem = GameItem.values()[itemIdx]
                        gameItem.use(game, ourColor, res)
                        ourPuzzle.inventory[slotNum] = 0
                        res.message = gameItem.name
                        res.game = game
                    }
                }
            }

            else -> {
                println("구현중인 명령어 : $message")
                println("targets = $targets")
            }
        }

        // 게임 끝났는지 마지막에 확인
        if ((ourPuzzle.isCompleted || yourPuzzle.isCompleted) && (game.isStarted && !game.isFinished)) {
            game.isFinished = true
            game.finishTime = Date()
            res.isFinished = game.isFinished
            res.game = game
            res.message = "SAVE_RECORD"

            res.redProgressPercent = game.redPuzzle?.calculateMixedProgress() ?: 0.0
            res.blueProgressPercent =
                if (game.gameType == "BATTLE") {
                    game.bluePuzzle?.calculateMixedProgress() ?: 0.0
                } else {
                    -1.0
                }
            res.redBundles = game.redPuzzle
                ?.bundles
                ?.values
                ?.map { it.toSet() } ?: emptyList()
            res.blueBundles =
                if (game.gameType.equals("BATTLE", ignoreCase = true)) {
                    game.bluePuzzle
                        ?.bundles
                        ?.values
                        ?.map { it.toSet() } ?: emptyList()
                } else {
                    emptyList()
                }

            sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", res)
            game.isStarted = false
            res.isStarted = false
            val waitingRoomId = game.roomId

            val room =
                puzzleRoomRepository.findById(waitingRoomId).orElseThrow {
                    IllegalArgumentException("PuzzleRoom not found for ID: $waitingRoomId")
                }

            room.roomStatus = "WAITING"
            puzzleRoomRepository.save(room)
            deleteGame(game.gameId)
        }

        // 진행도 추가
        if (game.gameType == "BATTLE") {
            if (ourColor == "RED") {
                res.redProgressPercent = calculateProgress(ourPuzzle) // 레드 팀 진행률
                res.blueProgressPercent = calculateProgress(yourPuzzle) // 블루 팀 진행률
            } else {
                res.blueProgressPercent = calculateProgress(ourPuzzle) // 블루 팀 진행률
                res.redProgressPercent = calculateProgress(yourPuzzle) // 레드 팀 진행률
            }
        } else {
            res.redProgressPercent = calculateProgress(ourPuzzle) // 협동 모드의 레드 팀 진행률
        }
        return res
    }

    private fun getNewPoint(
        puzzle: PuzzleBoard,
        std: Piece,
        target: Piece,
    ): Pair<Double, Double> {
        val (targetY, targetX) = puzzle.idxToCoordinate[target.index]!!
        val (stdY, stdX) = puzzle.idxToCoordinate[std.index]!!
        return Pair(
            std.position_x + (targetX - stdX) * puzzle.pieceSize,
            std.position_y + (targetY - stdY) * puzzle.pieceSize,
        )
    }

    private fun calculateProgress(puzzle: PuzzleBoard): Double {
        return puzzle.calculateMixedProgress() // PuzzleBoard의 혼합 진행률 계산 호출
    }

    private fun savePuzzle(game: Game) =
        runBlocking {
            val basicKey = "$gameKeyPrefix${game.gameId}"
            val jobList = mutableListOf<Deferred<Unit>>()
            game.redPuzzle?.let { it ->
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:redPuzzle:correctedCount", it.correctedCount.toString())
                    },
                )
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:redPuzzle:connectedEdges", it.connectedEdges.toString())
                    },
                )
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:redPuzzle:isCompleted", it.isCompleted.toString())
                    },
                )

                val boardJson = objectMapper.writeValueAsString(it.board)
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:redPuzzle:board", boardJson)
                    },
                )
                val correctedJson = objectMapper.writeValueAsString(it.isCorrected) // 2차원 배열 직렬화
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:redPuzzle:isCorrected", correctedJson)
                    },
                )
            }

            game.bluePuzzle?.let { it ->
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:bluePuzzle:correctedCount", it.correctedCount.toString())
                    },
                )
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:bluePuzzle:connectedEdges", it.connectedEdges.toString())
                    },
                )
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:bluePuzzle:isCompleted", it.isCompleted.toString())
                    },
                )

                val boardJson = objectMapper.writeValueAsString(it.board)
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:bluePuzzle:board", boardJson)
                    },
                )
                val correctedJson = objectMapper.writeValueAsString(it.isCorrected) // 2차원 배열 직렬화
                jobList.add(
                    async(Dispatchers.IO) {
                        redisTemplate.opsForValue().set("$basicKey:bluePuzzle:isCorrected", correctedJson)
                    },
                )
            }
            jobList.forEach { it.await() }
        }

    private val gameKeyPrefix = "Game:"

    private fun save(game: Game) {
        val basicKey = "$gameKeyPrefix${game.gameId}"
        val metaData =
            mapOf(
                "gameId" to game.gameId,
                "gameName" to game.gameName,
                "roomSize" to game.roomSize,
                "gameType" to game.gameType,
                "startTime" to game.startTime?.time,
                "finishTime" to game.finishTime?.time,
                "isStarted" to game.isStarted,
                "isFinished" to game.isFinished,
                "isSaved" to game.isSaved,
            )
        redisTemplate.opsForValue().set("$basicKey:meta", objectMapper.writeValueAsString(metaData))

        redisTemplate.opsForValue().set("$basicKey:redTeam", objectMapper.writeValueAsString(game.redTeam))
        redisTemplate.opsForValue().set("$basicKey:blueTeam", objectMapper.writeValueAsString(game.blueTeam))
        redisTemplate.opsForValue().set("$basicKey:players", objectMapper.writeValueAsString(game.players))

        game.picture?.let { redisTemplate.opsForValue().set("$basicKey:picture", objectMapper.writeValueAsString(it)) }

        game.admin?.let { redisTemplate.opsForValue().set("$basicKey:admin", objectMapper.writeValueAsString(it)) }

        game.redPuzzle?.let { it ->
            redisTemplate.opsForValue().set("$basicKey:redPuzzle:correctedCount", it.correctedCount.toString())
            redisTemplate.opsForValue().set("$basicKey:redPuzzle:connectedEdges", it.connectedEdges.toString())
            redisTemplate.opsForValue().set("$basicKey:redPuzzle:isCompleted", it.isCompleted.toString())
            for (idxToCord in it.idxToCoordinate) {
                redisTemplate.opsForHash<String, String>().put(
                    "$basicKey:redPuzzle:idxToCoordinate",
                    idxToCord.key.toString(),
                    ListStringUtils.listToString(idxToCord.value),
                )
            }

            val boardJson = objectMapper.writeValueAsString(it.board)
            redisTemplate.opsForValue().set("$basicKey:redPuzzle:board", boardJson)
            val correctedJson = objectMapper.writeValueAsString(it.isCorrected) // 2차원 배열 직렬화
            redisTemplate.opsForValue().set("$basicKey:redPuzzle:isCorrected", correctedJson)
        }

        game.bluePuzzle?.let { it ->

            redisTemplate.opsForValue().set("$basicKey:bluePuzzle:correctedCount", it.correctedCount.toString())
            redisTemplate.opsForValue().set("$basicKey:bluePuzzle:connectedEdges", it.connectedEdges.toString())
            redisTemplate.opsForValue().set("$basicKey:bluePuzzle:isCompleted", it.isCompleted.toString())
            for (idxToCord in it.idxToCoordinate) {
                redisTemplate.opsForHash<String, String>().put(
                    "$basicKey:bluePuzzle:idxToCoordinate",
                    idxToCord.key.toString(),
                    ListStringUtils.listToString(idxToCord.value),
                )
            }

            val boardJson = objectMapper.writeValueAsString(it.board)
            redisTemplate.opsForValue().set("$basicKey:bluePuzzle:board", boardJson)
            val correctedJson = objectMapper.writeValueAsString(it.isCorrected) // 2차원 배열 직렬화
            redisTemplate.opsForValue().set("$basicKey:bluePuzzle:isCorrected", correctedJson)
        }
    }

    private fun load(gameId: String): Game {
        val basicKey = "$gameKeyPrefix$gameId"

        class MapStringAnyTypeReference : TypeReference<Map<String, Any>>()
        // Retrieve and parse metadata
        val metaDataJson: String =
            redisTemplate.opsForValue().get("$basicKey:meta") as? String ?: throw Exception("Game not found")
        val metaData: Map<String, String> =
            objectMapper.readValue<Map<String, String>>(
                metaDataJson,
            )
        val gameId = metaData["gameId"] as String
        val gameName = metaData["gameName"] as String
        val roomSize = (metaData["roomSize"] as String).toInt()
        val gameType = metaData["gameType"] as String
        val startTime = (metaData["startTime"] as String?)?.let { Date(it.toLong()) }
        val finishTime = (metaData["finishTime"] as String?)?.let { Date(it.toLong()) }

        // Retrieve teams and players
        val redTeamJson = redisTemplate.opsForValue().get("$basicKey:redTeam").toString() as? String ?: "[]"
        val redTeam: MutableList<PlayerRequest> = objectMapper.readValue<MutableList<PlayerRequest>>(redTeamJson)

        val blueTeamJson = redisTemplate.opsForValue().get("$basicKey:blueTeam").toString() as? String ?: "[]"
        val blueTeam: MutableList<PlayerRequest> = objectMapper.readValue(blueTeamJson)

        val playersJson = redisTemplate.opsForValue().get("$basicKey:players").toString() as? String ?: "[]"
        val players: MutableList<User> = objectMapper.readValue(playersJson)

        // Retrieve optional fields
        val pictureJson = redisTemplate.opsForValue().get("$basicKey:picture").toString()
        val picture: Picture = objectMapper.readValue(pictureJson)
        val adminJson = redisTemplate.opsForValue().get("$basicKey:admin").toString()
        val admin: User = objectMapper.readValue(adminJson)

        // Helper function to load a puzzle
        fun loadPuzzle(prefix: String): PuzzleBoard? {
            val correctedCount =
                redisTemplate
                    .opsForValue()
                    .get("$prefix:correctedCount")
                    .toString()
                    .toInt() ?: 0
            val connectedEdges =
                redisTemplate
                    .opsForValue()
                    .get("$prefix:connectedEdges")
                    .toString()
                    .toInt() ?: 0

            val idxToCoordinateEntries = redisTemplate.opsForHash<String, String>().entries("$prefix:idxToCoordinate")
            val idxToCoordinate: MutableMap<Int, List<Int>> = mutableMapOf()
            for ((key, value) in idxToCoordinateEntries) {
                idxToCoordinate[key.toInt()] = ListStringUtils.stringToList(value).map { it.toString().toInt() }
            }

            val boardJson = redisTemplate.opsForValue().get("$prefix:board").toString() as? String ?: "[]"
            val board: MutableList<MutableList<Piece>> = objectMapper.readValue(boardJson)

            val isCorrectedJson = redisTemplate.opsForValue().get("$prefix:isCorrected").toString() as? String ?: "[]"
            val isCorrected: MutableList<MutableList<Boolean>> = objectMapper.readValue(isCorrectedJson)

            return PuzzleBoard().reload(picture, board, isCorrected, correctedCount, connectedEdges, idxToCoordinate)
        }

        // Retrieve puzzles
        val redPuzzle = loadPuzzle("$basicKey:redPuzzle")
        val bluePuzzle = loadPuzzle("$basicKey:bluePuzzle")

        // Construct and return the Game object
        return Game(
            gameId = gameId,
            gameName = gameName,
            roomSize = roomSize,
            gameType = gameType,
            startTime = startTime,
            finishTime = finishTime,
            isStarted = true,
            isFinished = false,
            isSaved = true,
            redTeam = redTeam,
            blueTeam = blueTeam,
            players = players,
            picture = picture,
            admin = admin,
            redPuzzle = redPuzzle,
            bluePuzzle = bluePuzzle,
        )
    }
}
