package com.puzzle.websocket.puzzle.dto

import com.puzzle.websocket.puzzle.dto.Picture
import com.puzzle.websocket.puzzle.dto.Piece

const val TOP = 0
const val RIGHT = 1
const val BOTTOM = 2
const val LEFT = 3

class PuzzleBoard {
    var picture: Picture? = null
    var idxToCoordinate: HashMap<Int, IntArray> = HashMap()
    var board: Array<Array<Array<Piece?>>> = arrayOf()

    var pieceSize: Int = 0
    var widthCnt: Int = 0
    var lengthCnt: Int = 0

    var bundles: MutableList<MutableSet<Piece>> = mutableListOf()
    var isCorrected: Array<BooleanArray> = arrayOf()
    var correctedCount: Int = 0
    var visited: Array<BooleanArray> = arrayOf()

    fun init(p: Picture): Array<Array<Array<Piece?>>> {
        picture = p

        // 조각 크기 및 개수 계산
        pieceSize = 100
        val levelSize = mapOf(1 to 500, 2 to 600, 3 to 800)
        val originHeight = p.length
        val originWidth = p.width
        val imgWidth = if (originHeight >= originWidth)
            (levelSize[3]!! * originWidth / originHeight / 100) * 100
        else
            levelSize[3]!!
        val imgHeight = if (originHeight >= originWidth)
            levelSize[3]!!
        else
            (levelSize[3]!! * originHeight / originWidth / 100) * 100


        widthCnt = (imgWidth / pieceSize.toDouble()).toInt()
        lengthCnt = (imgHeight / pieceSize.toDouble()).toInt()

        // 퍼즐 조각 초기화 및 고유 인덱스 할당
        board = Array(2) { Array(lengthCnt) { arrayOfNulls<Piece>(widthCnt) } }
        isCorrected = Array(lengthCnt) { BooleanArray(widthCnt) }
        idxToCoordinate = HashMap()
        var cnt = 0
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                board[0][i][j] = Piece(cnt)
                idxToCoordinate[cnt] = intArrayOf(i, j)
                val piece = board[0][i][j]!!

                piece.correctRightIndex = if (cnt + 1 >= widthCnt * (i + 1)) -1 else cnt + 1
                piece.correctLeftIndex = if (cnt - 1 < i * widthCnt) -1 else cnt - 1
                piece.correctTopIndex = if (cnt - widthCnt < 0) -1 else cnt - widthCnt
                piece.correctBottomIndex = if (cnt + widthCnt >= widthCnt * lengthCnt) -1 else cnt + widthCnt

                cnt++
            }
        }

        // 퍼즐 조각의 모양 설정
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                val now = board[0][i][j]!!
                val type = IntArray(4)

                when {
                    // 상단 변
                    i == 0 -> when {
                        // 좌상단 꼭짓점
                        j == 0 -> {
                            type[TOP] = 0
                            type[LEFT] = 0
                            type[RIGHT] = random(2)
                            type[BOTTOM] = random(2)
                        }
                        // 우상단 꼭짓점
                        j == widthCnt - 1 -> {
                            type[TOP] = 0
                            type[RIGHT] = 0
                            type[BOTTOM] = random(2)
                            type[LEFT] = if (board[0][i][j - 1]?.type?.get(RIGHT) == 2) 1 else 2
                        }
                        // 그 외 상단 변
                        else -> {
                            type[TOP] = 0
                            type[RIGHT] = random(2)
                            type[BOTTOM] = random(2)
                            type[LEFT] = if (board[0][i][j - 1]?.type?.get(RIGHT) == 2) 1 else 2
                        }
                    }
                    // 하단 변
                    i == lengthCnt - 1 -> when {
                        // 좌하단 꼭짓점
                        j == 0 -> {
                            type[TOP] = if (board[0][i - 1][j]?.type?.get(BOTTOM) == 2) 1 else 2
                            type[RIGHT] = random(2)
                            type[BOTTOM] = 0
                            type[LEFT] = 0
                        }
                        // 우하단 꼭짓점
                        j == widthCnt - 1 -> {
                            type[TOP] = if (board[0][i - 1][j]?.type?.get(BOTTOM) == 2) 1 else 2
                            type[RIGHT] = 0
                            type[BOTTOM] = 0
                            type[LEFT] = if (board[0][i][j - 1]?.type?.get(RIGHT) == 2) 1 else 2
                        }
                        // 그 외 하단 변
                        else -> {
                            type[TOP] = if (board[0][i - 1][j]?.type?.get(BOTTOM) == 2) 1 else 2
                            type[RIGHT] = random(2)
                            type[BOTTOM] = 0
                            type[LEFT] = if (board[0][i][j - 1]?.type?.get(RIGHT) == 2) 1 else 2
                        }
                    }
                    // 좌측 변
                    j == 0 -> {
                        type[TOP] = if (board[0][i - 1][j]?.type?.get(BOTTOM) == 2) 1 else 2
                        type[RIGHT] = random(2)
                        type[BOTTOM] = random(2)
                        type[LEFT] = 0
                    }
                    // 우측 변
                    j == widthCnt - 1 -> {
                        type[TOP] = if (board[0][i - 1][j]?.type?.get(BOTTOM) == 2) 1 else 2
                        type[RIGHT] = 0
                        type[BOTTOM] = random(2)
                        type[LEFT] = if (board[0][i][j - 1]?.type?.get(RIGHT) == 2) 1 else 2
                    }
                    // 가운데 부분
                    else -> {
                        type[TOP] = if (board[0][i - 1][j]?.type?.get(BOTTOM) == 2) 1 else 2
                        type[RIGHT] = random(2)
                        type[BOTTOM] = random(2)
                        type[LEFT] = if (board[0][i][j - 1]?.type?.get(RIGHT) == 2) 1 else 2
                    }
                }
                now.type = type
            }
        }

        correctedCount = 0
        randomArrange()
        return board
    }

    // 퍼즐 조각 결합 메서드
    fun addPiece(pieceList: List<Int>) {
        val set: MutableSet<Piece> = mutableSetOf()

        for (pieceIdx in pieceList) {
            val coord = idxToCoordinate[pieceIdx] ?: continue
            val x = board[0][coord[0]][coord[1]] ?: continue

            // 기존에 속한 뭉탱이 제거
            bundles.removeAll { it.contains(x) }

            // 플레이어의 보드에서 조각 제거
            for (i in 0 until lengthCnt) {
                for (j in 0 until widthCnt) {
                    if (board[1][i][j]?.index == x.index) {
                        board[1][i][j] = null
                        break
                    }
                }
            }

            // 결합 표시 및 집합에 추가
            isCorrected[coord[0]][coord[1]] = true
            set.add(x)
        }

        bundles.add(set)
        updatePieceCount()
    }

    // 조각 개수 업데이트 메서드
    fun updatePieceCount() {
        correctedCount = bundles.sumBy { it.size }
    }

    // 결합된 조각 분해 메서드
    fun deletePiece(targetIdx: Int) {
        val coord = idxToCoordinate[targetIdx] ?: return
        val (r, c) = coord
        if (!isCorrected[r][c]) return

        for (bundle in bundles) {
            if (bundle.removeIf { it.index == targetIdx }) {
                isCorrected[r][c] = false
                extracted(r, c)
                updatePieceCount()
                return
            }
        }
    }

    // 고립된 그룹 분해 탐색 메서드
    fun searchForGroupDisbandment() {
        visited = Array(lengthCnt) { BooleanArray(widthCnt) }

        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                if (isCorrected[i][j] && !visited[i][j]) {
                    visited[i][j] = true
                    val cnt = dfsForSearch(i, j)

                    if (cnt == 1) {
                        deletePiece(board[0][i][j]?.index ?: continue)
                    }
                }
            }
        }
    }

    // DFS 탐색 메서드
    private val dx = intArrayOf(1, -1, 0, 0)
    private val dy = intArrayOf(0, 0, -1, 1)


    fun dfsForSearch(
        r: Int,
        c: Int,
    ): Int {
        var cnt = 1

        for (i in 0 until 4) {
            val nr = r + dx[i]
            val nc = c + dy[i]

            if (nr in 0 until lengthCnt && nc in 0 until widthCnt) {
                if (isCorrected[nr][nc] && !visited[nr][nc]) {
                    visited[nr][nc] = true
                    cnt += dfsForSearch(nr, nc)
                }
            }
        }

        return cnt
    }


    private fun extracted(
        r: Int,
        c: Int,
    ) {
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                if (board[1][i][j] == null) {
                    board[1][i][j] = board[0][r][c]
                    return
                }
            }
        }
    }

    // 콤보 효과 메서드
    fun combo(pieceList: MutableList<Int>): List<Int> {
        val dx = intArrayOf(1, -1, 0, 0)
        val dy = intArrayOf(0, 0, -1, 1)

        val choiceSet: MutableSet<Int> = mutableSetOf()
        for (pieceIdx in pieceList) {
            val xy = idxToCoordinate[pieceIdx] ?: continue
            for (i in 0 until 4) {
                val nr = xy[0] + dx[i]
                val nc = xy[1] + dy[i]

                if (nr in 0 until lengthCnt && nc in 0 until widthCnt) {
                    if (!isCorrected[nr][nc]) {
                        board[0][nr][nc]?.index?.let { choiceSet.add(it) }
                    }
                }
            }
        }

        val choiceList = choiceSet.toMutableList()
        val comboPieces = mutableListOf<Int>()
        val comboCount = pieceList.size / 3

        repeat(comboCount) {
            if (choiceList.isEmpty()) return@repeat
            val randomPieceIdx = random(choiceList.size) - 1
            val chosenPiece = choiceList.removeAt(randomPieceIdx)
            comboPieces.add(chosenPiece)
        }

        pieceList.addAll(comboPieces)
        addPiece(pieceList)
        return comboPieces
    }

    // 퍼즐 보드 상태 출력 메서드
    fun print() {
        println("---------------------------------------")
        println("진행률 : ${(correctedCount.toDouble() / pieceSize.toDouble()) * 100}%")
        println("퍼즐 판 정보")
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                print("${board[0][i][j]?.index} ")
            }
            println()
        }

        println("퍼즐 위치 정보")
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                print("${board[1][i][j]?.index ?: "null"} ")
            }
            println()
        }

        println("맞춰진 조각 정보")
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                print("${isCorrected[i][j]} ")
            }
            println()
        }

        println("조각 뭉탱이들 정보")
        for (set in bundles) {
            println(set)
        }
    }

    // 퍼즐 조각 랜덤 배치 메서드
    fun randomArrange() {
        val list = mutableListOf<Piece?>()
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                if (!isCorrected[i][j]) {
                    list.add(board[0][i][j])
                } else {
                    list.add(null)
                }
            }
        }

        list.shuffle()
        val iterator = list.iterator()
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                board[1][i][j] = iterator.next()
            }
        }
    }


    fun random(range: Int): Int = (Math.random() * range).toInt() + 1

    // 최대공약수 계산 메서드
    companion object {
        fun gcd(
            a: Int,
            b: Int,
        ): Int = if (b == 0) a else gcd(b, a % b)
    }
}
