package com.puzzle.websocket.game.controller

import com.puzzle.websocket.game.domain.ResponseMessage
import com.puzzle.websocket.game.domain.SharePuzzle
import com.puzzle.websocket.game.domain.User
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
//    private val imageService: ImageService
) {
    private val BATTLE_TIMER = 60
    private var sessionId: String? = null
    private val waitingList: Queue<User> = ConcurrentLinkedQueue()

    // 세션 아이디 설정
    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        // System.out.println("MessageController.handleWebSocketConnectListener")
        // System.out.println(event.message.headers["simpSessionId"])
        sessionId = event.message.headers["simpSessionId"] as String?
    }
//
//    @EventListener
//    @Throws(InterruptedException::class)
//    fun handleDisconnectEvent(event: SessionDisconnectEvent) {
//        // System.out.println("MessageController.handleDisconnectEvent")
//        val accessor: StompHeaderAccessor = StompHeaderAccessor.wrap(event.message)
//        val sessionId: String = accessor.sessionId
//        val gameId: String? = gameService.sessionToGame?.get(sessionId)
//        val game: Game? = gameService.findById(gameId!!)
//
//        if (game == null) {
//            return
//        }
//
//        if (accessor.command == StompCommand.DISCONNECT) {
//            if (game.isFinished) {
//                println("${game.sessionToUser[sessionId]?.id} 님이 퇴장하십니다.")
//                game.exitPlayer(sessionId)
//                gameService.sessionToGame?.remove(sessionId)
//            } else {
//                if (!game.isStarted) {
//                    // 잠시 대기
//                    // Thread.sleep(5000)
//                    // if (game.isEmpty()) {
//                    //     println("진짜 나간것같아. 게임 지울게!")
//                    //     gameService.deleteRoom(gameId)
//                    // } else {
//                    //     println("새로고침이였어. 다시 연결한다!")
//                    //     return
//                    // }
//                    println("${game.sessionToUser[sessionId]?.id} 님이 퇴장하십니다.")
//                    game.exitPlayer(sessionId)
//                    gameService.sessionToGame?.remove(sessionId)
//                } else {
//                    println("어딜 나가 이자식아")
//                    return
//                }
//            }
//        }
//
//        sendingOperations.convertAndSend("/topic/game/room/$gameId", game)
//    }

    @MessageMapping("/{roomId}/game/enter")
    @Throws(Exception::class)
    fun enterGame(
        @DestinationVariable roomId: String,
    ) {
        sendingOperations.convertAndSend(
            "/topic/game/room/$roomId/init",
            gameService.findById(roomId)!!,
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

//        val res =
//            gameService.playGame(sharePuzzle).apply {
//                redProgressPercent = game.redPuzzle!!.correctedCount.toDouble() /
//                    (game.redPuzzle!!.lengthCnt * game.redPuzzle!!.widthCnt) * 100
//                blueProgressPercent = game.bluePuzzle!!.correctedCount.toDouble() /
//                    (game.bluePuzzle!!.lengthCnt * game.bluePuzzle!!.widthCnt) * 100
//                isFinished = game.isFinished
//                redBundles = game.redPuzzle!!.bundles
//                if (game.gameType == "BATTLE") {
//                    blueBundles = game.bluePuzzle!!.bundles
//                }
//            }
        val res = gameService.playGame(sharePuzzle).apply {

            // 혼합 방식 진행률 계산 반영
            redProgressPercent = game.redPuzzle?.calculateMixedProgress() ?: 0.0
            blueProgressPercent = if (game.gameType == "BATTLE") {
                game.bluePuzzle?.calculateMixedProgress() ?: 0.0
            } else {
                0.0
            }
            isFinished = game.isFinished
            redBundles = game.redPuzzle!!.bundles
            if (game.gameType == "BATTLE") {
                blueBundles = game.bluePuzzle!!.bundles
            }
        }

        // 해당 방의 모든 사용자에게 게임 상태 전송
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
                    time = BATTLE_TIMER - time
                }
                if (time >= 0) {
                    val timer = mapOf("time" to time)
                    sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", timer)
                } else {


                        val res = ResponseMessage()
                        res.isFinished = true

                        Thread.sleep(20)
                        sendingOperations.convertAndSend("/topic/game/room/${game.gameId}", res)

                }
            }
        }
    }
}
