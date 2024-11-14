package com.ssafy.puzzlepop.engine

import com.puzzle.websocket.puzzle.dto.Picture
import com.puzzle.websocket.puzzle.dto.Piece
import java.util.LinkedList
import kotlin.collections.HashMap

class PuzzleBoard {
    var picture: Picture? = null
    var coordinate: HashMap<Int, IntArray> = HashMap()
    var board: Array<Array<Piece?>> = arrayOf()
    var isCombinated: Array<BooleanArray> = arrayOf()
    var pieceSize: Int = 0
    var widthCnt: Int = 0
    var lengthCnt: Int = 0
    var bundles: MutableList<MutableSet<Piece>> = LinkedList()

    fun init(p: Picture): Array<Array<Piece?>> {
        picture = p

        // TODO: 1. 사진 비율에 따른 여러가지 피스 제공, 2. 소수인 사이즈를 가진 사진이 들어왔을 때 처리
        pieceSize = gcd(picture!!.width, picture!!.length)
        widthCnt = picture!!.width / pieceSize
        lengthCnt = picture!!.length / pieceSize

        // 퍼즐 조각 초기화
        board = Array(lengthCnt) { arrayOfNulls<Piece>(widthCnt) }
        isCombinated = Array(lengthCnt) { BooleanArray(widthCnt) }
        coordinate = HashMap()

        var cnt = 0
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                board[i][j] = Piece(cnt)
                coordinate[cnt] = intArrayOf(i, j)

                board[i][j]?.correctRightIndex = if (cnt + 1 >= widthCnt * (i + 1)) -1 else cnt + 1
                board[i][j]?.correctLeftIndex = if (cnt - 1 < i * widthCnt) -1 else cnt - 1
                board[i][j]?.correctTopIndex = if (cnt - widthCnt < 0) -1 else cnt - widthCnt
                board[i][j]?.correctBottomIndex = if (cnt + widthCnt >= widthCnt * lengthCnt) -1 else cnt + widthCnt

                cnt++
            }
        }

        // 퍼즐 생성 알고리즘 적용
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                val now = board[i][j]
                val type = IntArray(4)

                when {
                    // 상단 변
                    i == 0 ->
                        when {
                            // 좌상단 꼭짓점
                            j == 0 -> {
                                type[0] = 0
                                type[3] = 0
                                type[1] = random(2)
                                type[2] = random(2)
                            }
                            // 우상단 꼭짓점
                            j == widthCnt - 1 -> {
                                type[0] = 0
                                type[1] = 0
                                type[2] = random(2)
                                type[3] = if (board[i][j - 1]?.type?.get(1) == 2) 1 else 2
                            }
                            // 그 외 상단 변
                            else -> {
                                type[0] = 0
                                type[1] = random(2)
                                type[2] = random(2)
                                type[3] = if (board[i][j - 1]?.type?.get(1) == 2) 1 else 2
                            }
                        }
                    // 하단 변
                    i == lengthCnt - 1 ->
                        when {
                            // 좌하단 꼭짓점
                            j == 0 -> {
                                type[2] = 0
                                type[3] = 0
                                type[0] = if (board[i - 1][j]?.type?.get(2) == 2) 1 else 2
                                type[1] = random(2)
                            }
                            // 우하단 꼭짓점
                            j == widthCnt - 1 -> {
                                type[0] = if (board[i - 1][j]?.type?.get(2) == 2) 1 else 2
                                type[1] = 0
                                type[2] = 0
                                type[3] = if (board[i][j - 1]?.type?.get(1) == 2) 1 else 2
                            }
                            // 그 외 하단 변
                            else -> {
                                type[0] = if (board[i - 1][j]?.type?.get(2) == 2) 1 else 2
                                type[1] = random(2)
                                type[2] = 0
                                type[3] = if (board[i][j - 1]?.type?.get(1) == 2) 1 else 2
                            }
                        }
                    // 좌측 변
                    j == 0 -> {
                        type[0] = if (board[i - 1][j]?.type?.get(2) == 2) 1 else 2
                        type[1] = random(2)
                        type[2] = random(2)
                        type[3] = 0
                    }
                    // 우측 변
                    j == widthCnt - 1 -> {
                        type[0] = if (board[i - 1][j]?.type?.get(2) == 2) 1 else 2
                        type[1] = 0
                        type[2] = random(2)
                        type[3] = if (board[i][j - 1]?.type?.get(1) == 2) 1 else 2
                    }
                    // 가운데 부분
                    else -> {
                        type[0] = if (board[i - 1][j]?.type?.get(2) == 2) 1 else 2
                        type[1] = random(2)
                        type[2] = random(2)
                        type[3] = if (board[i][j - 1]?.type?.get(1) == 2) 1 else 2
                    }
                }
                now?.type = type
            }
        }

        return board
    }

    // 퍼즐 조각 결합
    fun addPiece(list: List<Int>) {
        val set: MutableSet<Piece> = HashSet()
        for (pieceIdx in list) {
            val x = board[coordinate[pieceIdx]!![0]][coordinate[pieceIdx]!![1]]
            bundles.removeAll { it.contains(x) }
            x?.let { set.add(it) }
        }
        bundles.add(set)
    }

    fun random(range: Int): Int = (Math.random() * range).toInt() + 1

    companion object {
        fun gcd(
            a: Int,
            b: Int,
        ): Int = if (b == 0) a else gcd(b, a % b)
    }
}
