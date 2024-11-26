package com.puzzle.websocket.game.controller

import com.puzzle.websocket.game.domain.ResponseMessage
import com.puzzle.websocket.game.domain.SharePuzzle
import com.puzzle.websocket.game.domain.User
import com.puzzle.websocket.game.dto.response.InventoryResponse
import com.puzzle.websocket.game.service.GameService
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionConnectEvent
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

@Controller
@EnableScheduling
class GameController(
    private val gameService: GameService,
    private val sendingOperations: SimpMessageSendingOperations,
) {
    private val battleTimer = 180
    private var sessionId: String? = null
    private val waitingList: Queue<User> = ConcurrentLinkedQueue()

    // 세션 아이디 설정
    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        sessionId = event.message.headers["simpSessionId"] as String?
    }

    @MessageMapping("/{roomId}/game/enter")
    @Throws(Exception::class)
    fun enterGame(
        @DestinationVariable roomId: String,
    ) {
        val game = gameService.findById(roomId)!!
        sendingOperations.convertAndSend(
            "/topic/game/room/$roomId/init",
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
            },
        )
    }

    @MessageMapping("/game/puzzle")
    @Throws(Exception::class)
    fun puzzle(sharePuzzle: SharePuzzle) {
        val game = gameService.findById(sharePuzzle.roomId) ?: return

        if (!game.isStarted) {
            println("게임이 시작되지 않았습니다. 명령을 무시합니다.")
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

                    if(Math.abs(redProgressPercent - blueProgressPercent) >= 25){
                        val targetTeam = if (redProgressPercent > blueProgressPercent) { "BLUE" } else { "RED" }
                        val targetPuzzle = if (redProgressPercent > blueProgressPercent) { game.bluePuzzle } else { game.redPuzzle }!!
                        // item frame add
                        if (!targetPuzzle.addedFrame){
                            targetPuzzle.addedFrame = true
                            targetPuzzle.addItem(5)
                            sendingOperations.convertAndSend(
                                "/topic/game/room/${game.gameId}/help",
                                InventoryResponse(
                                    team = targetTeam,
                                    inventory = targetPuzzle.inventory,
                                    fitPieceIndex = -1,
                                )
                            )
                        }
                    }
                }
            }

        sendingOperations.convertAndSend("/topic/game/room/${sharePuzzle.roomId}", res)
    }

    //  서버 타이머 제공
    @Scheduled(fixedRate = 1000)
    @Throws(Exception::class)
    fun sendServerTime() {
        val allRooms = gameService.findAllCooperationRoom() + gameService.findAllBattleRoom()
        for (game in allRooms.reversed()) {
            if (game.isStarted) {
                var time = game.getTime()
                if (game.gameType == "BATTLE") {
                    time = battleTimer - time
                }
                if (time >= 0) {
                    val timer = mapOf("time" to time)
                    sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", timer)
                } else {
                    val res = ResponseMessage()
                    res.isFinished = true

                    Thread.sleep(20)
                    sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", res)
                    gameService.deleteGame(game.gameId)
                }
            }
        }
    }
}
