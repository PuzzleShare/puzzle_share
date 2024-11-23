package com.puzzle.websocket.game.enums

import com.puzzle.websocket.game.domain.Game
import com.puzzle.websocket.game.domain.PuzzleBoard
import com.puzzle.websocket.game.domain.ResponseMessage

enum class GameItem(
    private val func: (Game, String, ResponseMessage) -> Unit
) {
    // index 맞춰주기 위함
    NONE({ game, team, res -> }),
    FIRE({ game, team, res ->
        // todo item 로직 작성
        // todo 아이템 사용 후 res에 값 세팅
    }),
    MUD({ game, team, res ->
        // front 에서 구현
    }),
    TYPHOON({ game, team, res ->

    }),
    BROOMSTICK({ game, team, res ->

    }),
    FRAME({ game, team, res ->

    });

    fun use(game: Game, team: String, res: ResponseMessage) = func.invoke(game, team, res)
}