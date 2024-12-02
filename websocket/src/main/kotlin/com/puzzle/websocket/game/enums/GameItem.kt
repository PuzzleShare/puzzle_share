package com.puzzle.websocket.game.enums

import com.puzzle.websocket.game.domain.Game
import com.puzzle.websocket.game.domain.Piece
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
        val (targetPuzzle, targetTeam) = if (team.uppercase() == "RED") {
            game.bluePuzzle!! to "BLUE"
        } else {
            game.redPuzzle!! to "RED"
        }

        val targets = targetPuzzle.bundles.values
            .filter { it.size == 1 }

        targets.forEach { it.forEach(targetPuzzle::randomPosition) }

        res.targetList = targets.map { it.elementAt(0).index }
        res.targets = targetTeam
    }),
    BROOMSTICK({ game, team, res ->
        val (targetPuzzle, targetTeam) = if (team.uppercase() == "RED") {
            game.bluePuzzle!! to "BLUE"
        } else {
            game.redPuzzle!! to "RED"
        }

        val targets = targetPuzzle.bundles.values
            .filter { it.size == 1 }
        val (x, y) = targetPuzzle.getCanvasCenter()
        targets.forEach {
            it.forEach {
                val randomX = (Math.random() * 20).toInt()
                it.position_x = x + randomX * if (randomX % 2 == 0) { 1 } else { -1 }
                val randomY = (Math.random() * 20).toInt()
                it.position_y = y + randomY * if (randomY % 2 == 0) { 1 } else { -1 }
            }
        }

        res.targetList = targets.map { it.elementAt(0).index }
        res.targets = targetTeam
    }),
    FRAME({ game, team, res ->
        val (targetPuzzle, targetTeam) = if (team.uppercase() == "RED") {
            game.redPuzzle!! to "RED"
        } else {
            game.bluePuzzle!! to "BLUE"
        }

        val board = targetPuzzle.board
        val maxRow = board.size
        val maxCol = board[0].size
        // 둘레 피스 그룹화
        for (i in 0 until maxCol - 1){
            targetPuzzle.addPiece(
                listOf(board[0][i].index, board[0][i+1].index)
            )
        }
        for (i in 0 until maxRow-1){
            targetPuzzle.addPiece(
                listOf(board[i][maxCol-1].index, board[i+1][maxCol-1].index)
            )
        }
        for (i in maxCol-1 downTo 1){
            targetPuzzle.addPiece(
                listOf(board[maxRow-1][i].index, board[maxRow-1][i-1].index)
            )
        }
        for (i in maxRow-1 downTo 1){
            targetPuzzle.addPiece(
                listOf(board[i][0].index, board[i-1][0].index)
            )
        }

        // 그룹화한 피스 위치 조정
        val pieceSize = targetPuzzle.picture!!.pieceSize
        val cnt = targetPuzzle.picture!!.widthPieceCnt
        var (startX, startY) = targetPuzzle.getCanvasCenter()
        startX -= targetPuzzle.picture!!.imgWidth / 2
        startY -= targetPuzzle.picture!!.imgHeight / 2
        val targetBundle = targetPuzzle.bundles[targetPuzzle.board[0][0].bundleNum]!!
        targetBundle.forEach {
            it.position_x = (it.index % cnt) * pieceSize + startX
            it.position_y = (it.index / cnt) * pieceSize + startY
        }

        res.targetList = targetBundle.map { it.index }
        res.targets = targetTeam
    });

    fun use(game: Game, team: String, res: ResponseMessage) = func.invoke(game, team, res)
}