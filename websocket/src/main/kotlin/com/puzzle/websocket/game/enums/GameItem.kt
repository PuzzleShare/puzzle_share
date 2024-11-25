package com.puzzle.websocket.game.enums

import com.puzzle.websocket.game.domain.Game
import com.puzzle.websocket.game.domain.ResponseMessage
import java.util.PriorityQueue

enum class GameItem(
    private val func: (Game, String, ResponseMessage) -> Unit
) {
    // index 맞춰주기 위함
    NONE({ game, team, res -> }),
    FIRE(fun(game, team, res) {
        val (targetPuzzle, targetTeam) = if (team.uppercase() == "RED") {
            game.bluePuzzle!! to "BLUE"
        } else {
            game.redPuzzle!! to "RED"
        }

        val keyList = targetPuzzle.bundles
            .map { it.key to it.value.size }
            .sortedBy { it.second }
            .reversed()
        val bundleKey = keyList[0].first
        val targetBundle = targetPuzzle.bundles[bundleKey]
        if (targetBundle == null || targetBundle.size == 1) {
            res.targetList = null
        } else {
            val pieces = targetBundle.toMutableList().filter { it.index != bundleKey }
            val piece = pieces[0]
            val pieceGroup = targetBundle.groupBy { it.index }
            val targets = mutableListOf(piece)
            piece.correctIndex
                .filter { it != -1 && it != bundleKey }
                .filter(pieceGroup::contains)
                .forEach { targets.add(pieceGroup[it]!![0]) }

            for (p in targets) {
                targetPuzzle.deletePiece(bundleKey, p)
                targetPuzzle.bundles[p.index] = mutableSetOf(p)
                p.bundleNum = p.index
                targetPuzzle.randomPosition(p)
            }

            targetPuzzle.bundleSplit(bundleKey)

            res.targetList = targets.map { it.index }
        }
        res.targets = targetTeam
    }),
    MUD({ game, team, res ->
        // front 에서 구현
        val targetTeam = if (team.uppercase() == "RED") {
            "BLUE"
        } else {
            "RED"
        }
        res.targets = targetTeam
        res.targetList = null
    }),
    TYPHOON({ game, team, res ->

    }),
    BROOMSTICK({ game, team, res ->

    }),
    FRAME({ game, team, res ->

    });

    fun use(game: Game, team: String, res: ResponseMessage) = func.invoke(game, team, res)
}