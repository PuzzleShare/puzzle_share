package com.puzzle.websocket.puzzle.dto

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class Game(
    // nullable 타입: String?, User?와 같이
    // ?가 붙은 타입은 null을 허용
    // gameId: String?는 gameId가 null이 될 수 있음을 의미
    var gameId: String? = null,
    var gameName: String? = null,
    var admin: User? = null,
    var redTeam: Team = Team(mutableListOf()),
    var blueTeam: Team = Team(mutableListOf()),
    var redPuzzle: PuzzleBoard? = null,
    var bluePuzzle: PuzzleBoard? = null,
    var startTime: Date = Date(),
    var isStarted: Boolean = false,
    var sessionToUser: HashMap<String, User> = HashMap(),
) {

    // 팀 변경 눌렀을 때, 내 팀 색을 확인한 후에 그 팀에서 제거하고 반대팀에 추가하는 형태로 구현
    fun changeTeam(user: User) {
        if (redTeam.isIn(user)) {
            redTeam.deletePlayer(user)
            blueTeam.addPlayer(user)
        } else {
            blueTeam.deletePlayer(user)
            redTeam.addPlayer(user)
        }
    }

    // 사용자가 게임에서 나갈 때, 팀에서 제거하고 세션에서 완전히 제거
    // sessionToUser 맵은 특정 게임방에 연결된 세션과 사용자 간의 매핑을 나타내고 있다.
    // 따라서 sessionToUser에서 sessionId를 제거하는 것은 사용자가 해당 게임방에서 나갔음을 의미하고,
    // 그 게임방 내에서 더 이상 사용자의 세션 정보를 추적하지 않겠다는 뜻
    fun exitPlayer(sessionId: String) {
        val user = sessionToUser[sessionId] ?: return

        if (redTeam.players.contains(user)) {
            redTeam.deletePlayer(user)
        } else {
            blueTeam.deletePlayer(user)
        }
        sessionToUser.remove(sessionId)
    }


    // sessionToUser[sessionId] = user로 sessionId와 user를 매핑하여 저장

    fun enterPlayer(user: User, sessionId: String): Boolean {
        sessionToUser[sessionId] = user

        // 이미 방 안에 들어와있는 유저가 또 그 게임 방을 들어가려는 요청을 하지 못할텐데
        // 뭐하러 검사를 또 하지?라는 생각이 들어서 gpt에게 질문.
        // 1. 네트워크 오류나 지연으로 사용자가 동일한 입장 요청을 여러 번 보내는 경우,
        // 서버가 이를 모두 받아 처리하게 되면 같은 사용자가 중복으로 추가되는 오류가 발생할 수 있다.
        // 2. 다중 탭/브라우저 문제: 일부 사용자가 여러 브라우저 탭이나 기기에서 게임방에 접속을 시도할 수 있다.
        // 3. 클라이언트 코드에서 버그나 기타 원인으로 사용자가 중복으로 입장 요청을 보낼 수도 있다.
        // 이미 게임 방에 입장한 사용자가 다시 입장을 요청하는 것을 방지하기 위한 로직
        if (redTeam.players.contains(user) || blueTeam.players.contains(user)) {
            return true
        }

        return if (redTeam.players.size <= 3) {
            redTeam.addPlayer(user)
            true
        } else {
            if (blueTeam.players.size <= 3) {
                blueTeam.addPlayer(user)
                true
            // 두 팀 모두 여유가 없는 경우 false를 이용하여 방에 들어갈 수 없음을 알리면 됨.
            } else {
                false
            }
        }
    }

    fun start() {
        // Picture 객체를 생성하여 퍼즐 보드를 초기화할 때 사용
        val picture = Picture(1000, 551, ".")
        redPuzzle = PuzzleBoard()
        bluePuzzle = PuzzleBoard()
        redPuzzle?.init(picture)
        bluePuzzle?.init(picture)

        val dateFormat = SimpleDateFormat("HH:mm:ss")
        startTime = Date()
        println(startTime)
        isStarted = true
        println("------------------게임 시작-------------------")
        redPuzzle?.print()
        bluePuzzle?.print()
    }

    fun getTime(): Long {
        val nowTime = Date()
        val start = startTime.time
        val now = nowTime.time
        return (now - start) / 1000
    }

    fun isEmpty(): Boolean {
        return (redTeam.players.size + blueTeam.players.size) == 0
    }

    companion object {
        fun create(name: String, userId: String): Game {
            val map = HashMap<String, User>()
            val game = Game()
            val uuid = UUID.randomUUID().toString()

            game.redTeam = Team(mutableListOf())
            game.blueTeam = Team(mutableListOf())
            game.sessionToUser = map

            game.gameId = uuid
            game.gameName = name
            game.startTime = Date()

            println("$name 배틀 방 생성 / id = $uuid")
            return game
        }
    }
}