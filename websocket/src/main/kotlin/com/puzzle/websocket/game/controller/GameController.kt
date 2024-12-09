package com.puzzle.websocket.game.controller

import com.puzzle.websocket.game.domain.ResponseMessage
import com.puzzle.websocket.game.domain.SharePuzzle
import com.puzzle.websocket.game.domain.User
import com.puzzle.websocket.game.dto.response.InventoryResponse
import com.puzzle.websocket.game.dto.response.PointerMoveDTO
import com.puzzle.websocket.game.service.GameService
import com.puzzle.websocket.room.dto.request.PlayerRequest
import com.puzzle.websocket.room.repository.PuzzleRoomRepository
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionConnectEvent
import java.util.Date
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.jvm.optionals.getOrNull

@Controller
@EnableScheduling
class GameController(
    private val gameService: GameService,
    private val puzzleRoomRepository: PuzzleRoomRepository,
    private val sendingOperations: SimpMessageSendingOperations,
) {
    private val battleTimer = 300
    private var sessionId: String? = null
    private val waitingList: Queue<User> = ConcurrentLinkedQueue()
    private val mouseData = mutableMapOf<String, MutableList<PointerMoveDTO>>()

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        sessionId = event.message.headers["simpSessionId"] as String?
    }

    @MessageMapping("/room/{roomId}/start")
    fun startGame(
        @DestinationVariable("roomId") roomId: String,
        playerRequest: PlayerRequest,
    ) {
        gameService.gameStart(roomId, playerRequest)
    }

    @MessageMapping("/{roomId}/game/enter")
    @Throws(Exception::class)
    fun enterGame(
        @DestinationVariable roomId: String,
    ) {
        val game = gameService.findById(roomId)!!
        val res =
            ResponseMessage(game = game).apply {
                // 혼합 방식 진행률 계산 반영
                redProgressPercent = game.redPuzzle?.calculateMixedProgress() ?: 0.0
                blueProgressPercent =
                    if (game.gameType == "BATTLE") {
                        game.bluePuzzle?.calculateMixedProgress() ?: 0.0
                    } else {
                        0.0
                    }
                isFinished = game.isFinished
                redBundles = game.redPuzzle
                    ?.bundles
                    ?.values
                    ?.map { it.toSet() } ?: emptyList()
                if (game.gameType.equals("BATTLE", ignoreCase = true)) {
                    blueBundles = game.bluePuzzle
                        ?.bundles
                        ?.values
                        ?.map { it.toSet() } ?: emptyList()
                }
            }
        sendingOperations.convertAndSend("/topic/game/room/$roomId/init", res)

        if (game.gameId in mouseData){
            sendingOperations.convertAndSend("/topic/game/${game.gameId}/pointer/init", mouseData[game.gameId]!!)
        }else{
            val pointerMoveDTOS =
                game.redTeam.map { PointerMoveDTO.of(it, "red", "red") } +
                        game.blueTeam.map { PointerMoveDTO.of(it, "blue", "blue") }
            mouseData[game.gameId] = pointerMoveDTOS.toMutableList()
            sendingOperations.convertAndSend("/topic/game/${game.gameId}/pointer/init", pointerMoveDTOS)
        }
    }

    @MessageMapping("/game/{gameId}/exit")
    @Throws(Exception::class)
    fun exitGame(
        @DestinationVariable gameId: String,
        playerRequest: PlayerRequest,
    ) {
        gameService.exitGame(gameId, playerRequest)
    }


    @MessageMapping("/game/puzzle")
    @Throws(Exception::class)
    fun puzzle(sharePuzzle: SharePuzzle) {
        val game = gameService.findById(sharePuzzle.roomId) ?: return

        if (!game.isStarted) {
            return
        }

        val res =
            gameService.playGame(sharePuzzle).apply {
                // 혼합 방식 진행률 계산 반영
                redProgressPercent = game.redPuzzle?.calculateMixedProgress() ?: 0.0
                blueProgressPercent =
                    if (game.gameType == "BATTLE") {
                        game.bluePuzzle?.calculateMixedProgress() ?: 0.0
                    } else {
                        0.0
                    }
                isFinished = game.isFinished
                redBundles = game.redPuzzle
                    ?.bundles
                    ?.values
                    ?.map { it.toSet() } ?: emptyList()
                if (game.gameType.equals("BATTLE", ignoreCase = true)) {
                    blueBundles = game.bluePuzzle
                        ?.bundles
                        ?.values
                        ?.map { it.toSet() } ?: emptyList()

                    if (Math.abs(redProgressPercent - blueProgressPercent) >= 25) {
                        val targetTeam =
                            if (redProgressPercent > blueProgressPercent) {
                                "BLUE"
                            } else {
                                "RED"
                            }
                        val targetPuzzle =
                            if (redProgressPercent > blueProgressPercent) {
                                game.bluePuzzle
                            } else {
                                game.redPuzzle
                            }!!
                        // item frame add
                        if (!targetPuzzle.addedFrame) {
                            targetPuzzle.addedFrame = true
                            targetPuzzle.addItem(5)
                            sendingOperations.convertAndSend(
                                "/topic/game/room/${game.gameId}/help",
                                InventoryResponse(
                                    team = targetTeam,
                                    inventory = targetPuzzle.inventory,
                                    fitPieceIndex = -1,
                                ),
                            )
                        }
                    }
                }
            }

        if (!innerSandMessage.contains(sharePuzzle.message))
        {
            sendingOperations.convertAndSend("/topic/game/room/${sharePuzzle.roomId}", res)
        }
    }

    private val innerSandMessage = setOf("MOUSE_DRAG")

    @MessageMapping("/game/{gameId}/mouse")
    @Throws(Exception::class)
    fun pointerMove(
        @DestinationVariable
        gameId: String,
        pointerMoveDTO: PointerMoveDTO,
    ) {
        mouseData[gameId]?.let {
            for (p in it){
                if (p.playerId == pointerMoveDTO.playerId){
                    p.x = pointerMoveDTO.x
                    p.y = pointerMoveDTO.y
                    break
                }
            }
        }
    }

    //  서버 타이머 제공
    @Scheduled(fixedRate = 1000)
    @Throws(Exception::class)
    fun sendServerTime() {
        val allRooms = gameService.findAllBattleRoom()
        for (game in allRooms.reversed()) {
            if (game.isStarted && !game.isFinished) {
                var time = game.getTime()
                if (game.gameType == "BATTLE") {
                    time = game.battleTimer - time
                }
                if (time >= 0) {
                    val timer = mapOf("time" to time)
                    sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", timer)
                } else {
                    game.finishTime = Date()
                    val res =
                        ResponseMessage(game = game).apply {
                            redProgressPercent = game.redPuzzle?.calculateMixedProgress() ?: 0.0
                            blueProgressPercent =
                                if (game.gameType == "BATTLE") {
                                    game.bluePuzzle?.calculateMixedProgress() ?: 0.0
                                } else {
                                    -1.0
                                }
                            redBundles = game.redPuzzle
                                ?.bundles
                                ?.values
                                ?.map { it.toSet() } ?: emptyList()
                            blueBundles =
                                if (game.gameType.equals("BATTLE", ignoreCase = true)) {
                                    game.bluePuzzle
                                        ?.bundles
                                        ?.values
                                        ?.map { it.toSet() } ?: emptyList()
                                } else {
                                    emptyList()
                                }
                            message = "SAVE_RECORD"
                        }
                    game.isFinished = true
                    res.isFinished = true
                    res.battleTimer = game.battleTimer
                    sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", res)
                    game.isStarted = false
                    res.isStarted = false

                    // room 상태 변경
                    puzzleRoomRepository.findById(game.roomId).getOrNull()?.let {
                        it.roomStatus = "WAITING"
                        puzzleRoomRepository.save(it)
                    }

                    Thread.sleep(20)
                    gameService.deleteGame(game.gameId)
                }
            }
        }
    }

    // 60초에 한번씩 방 청소
    @Scheduled(fixedRate = 60000)
    fun deleteGame() {
        val allRoom = gameService.findAllBattleRoom()
        for (i in allRoom.indices.reversed()) {
            if (allRoom[i].isFinished && !allRoom[i].isStarted) {
                gameService.deleteGameRoom(allRoom[i].gameId)
                mouseData.remove(allRoom[i].gameId)
            }
        }
    }

    @Scheduled(fixedRate = 100)
    fun mouseMove() {
        gameService.findAllBattleRoom()
        .forEach {
            if(!it.isFinished && it.gameId in mouseData){
                sendingOperations.convertAndSend("/topic/game/${it.gameId}/mouse", mouseData[it.gameId]!!)
            }
        }
    }
}
