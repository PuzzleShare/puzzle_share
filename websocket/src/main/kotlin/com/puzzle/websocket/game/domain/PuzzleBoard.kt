package com.puzzle.websocket.game.domain

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class PuzzleBoard {
    var picture: Picture? = null // 퍼즐에 쓰이는 사진
    var idxToCoordinate: HashMap<Int, IntArray> = HashMap()

    // 조각들이 들어있는 2차원 배열
    lateinit var board: Array<Array<Piece>>
    var pieceSize: Int = 0 // 조각 크기
    var widthCnt: Int = 0 // 가로 조각 개수
    var lengthCnt: Int = 0 // 세로 조각 개수

    // 조합된 퍼즐 뭉탱이들
    val bundles = Collections.synchronizedList(mutableListOf<MutableSet<Piece>>())
    lateinit var isCorrected: Array<BooleanArray> // 조합된 퍼즐인지 확인하는 2차원 배열
    var correctedCount: Int = 0 // 현재까지 맞춘 개수
    var isCompleted: Boolean = false
        private set

    // 랜덤 타입 적용에 쓰일 인덱스 상수
    val TOP = 0
    val RIGHT = 1
    val BOTTOM = 2
    val LEFT = 3

    var visited: Array<BooleanArray> = arrayOf()
    val CANVAS_WIDTH = 1000
    val CANVAS_LENGTH = 750

    // 방향 이동을 위한 배열
    val dx = arrayOf(1, -1, 0, 0)
    val dy = arrayOf(0, 0, -1, 1)

    // 퍼즐 판 초기화
    fun init(p: Picture, gameType: String): Array<Array<Piece>> {
        picture = p
        pieceSize = p.pieceSize
        widthCnt = p.widthPieceCnt
        lengthCnt = p.lengthPieceCnt

        board = Array(lengthCnt) { Array(widthCnt) { Piece(0) } }
        isCorrected = Array(lengthCnt) { BooleanArray(widthCnt) }
        var cnt = 0

        // 고유 인덱스 할당 및 주변 퍼즐에 대한 고유 인덱스 정보 초기화
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                val piece = Piece(cnt)
                board[i][j] = piece
                idxToCoordinate[cnt] = intArrayOf(i, j)

                piece.correctRightIndex = if (j + 1 < widthCnt) cnt + 1 else -1
                piece.correctLeftIndex = if (j - 1 >= 0) cnt - 1 else -1
                piece.correctTopIndex = if (i - 1 >= 0) cnt - widthCnt else -1
                piece.correctBottomIndex = if (i + 1 < lengthCnt) cnt + widthCnt else -1

                cnt++
            }
        }

        // 퍼즐 생성 알고리즘 적용
        generatePuzzlePieces()

        correctedCount = 0
        return board
    }

    // 퍼즐 조각 생성 알고리즘
    private fun generatePuzzlePieces() {
        val randomVisited = BooleanArray(widthCnt * lengthCnt)
        val totalPieces = widthCnt * lengthCnt
        val random = Random()

        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                val now = board[i][j]
                val type = IntArray(4)

                // 각 변에 대한 타입 결정
                type[TOP] = if (i == 0) 0 else invertType(board[i - 1][j].type[BOTTOM])
                type[BOTTOM] = if (i == lengthCnt - 1) 0 else randomType()
                type[LEFT] = if (j == 0) 0 else invertType(board[i][j - 1].type[RIGHT])
                type[RIGHT] = if (j == widthCnt - 1) 0 else randomType()

                now.type = type

                // 랜덤 위치 지정
                var idx: Int
                do {
                    idx = random.nextInt(totalPieces)
                } while (randomVisited[idx])
                randomVisited[idx] = true

                val (randI, randJ) = idxToCoordinate[idx]!!
                val randomPiece = board[randI][randJ]
                val x = (CANVAS_WIDTH / 2 - pieceSize / 2 + pieceSize * ((j * 2) + (i % 2)) - picture!!.imgWidth + 50).toDouble()
                val y = (CANVAS_LENGTH / 2 - pieceSize / 2 + pieceSize * i - picture!!.imgHeight / 2).toDouble()

                randomPiece.position_x = x
                randomPiece.position_y = y
            }
        }

        // 타입 변환 (-1, 1)
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                board[i][j].type = board[i][j].type.map {
                    when (it) {
                        1 -> -1
                        2 -> 1
                        else -> 0
                    }
                }.toIntArray()
            }
        }
    }

    // 타입 반전 함수
    private fun invertType(type: Int): Int {
        return when (type) {
            1 -> 2
            2 -> 1
            else -> randomType()
        }
    }

    // 랜덤 타입 생성
    private fun randomType(): Int {
        return Random().nextInt(2) + 1 // 1 또는 2 반환
    }

    // 퍼즐 조각 결합
    fun addPiece(pieceList: List<Int>) {
        val set = HashSet<Piece>()

        for (pieceIdx in pieceList) {
            if (pieceIdx == -1) continue

            val (i, j) = idxToCoordinate[pieceIdx]!!
            val piece = board[i][j]

            // 기존에 포함된 뭉치가 있으면 합침
            val iterator = bundles.iterator()
            while (iterator.hasNext()) {
                val bundle = iterator.next()
                if (bundle.contains(piece)) {
                    set.addAll(bundle)
                    iterator.remove()
                }
            }

            // 결합 표시
            isCorrected[i][j] = true
            set.add(piece)
        }

        // 새로운 뭉치 추가
        bundles.add(set)
        updatePieceCount()

        // 퍼즐 완성 여부 체크
        if (correctedCount == widthCnt * lengthCnt && bundles.size == 1) {
            isCompleted = true
            println("게임 끝!")
        }
    }

    // 맞춘 조각 수 업데이트
    private fun updatePieceCount() {
        correctedCount = bundles.sumBy { it.size }
    }

    // 결합된 조각 삭제
    fun deletePiece(targetIdx: Int): DoubleArray? {
        val (i, j) = idxToCoordinate[targetIdx]!!
        if (!isCorrected[i][j]) return null

        isCorrected[i][j] = false

        for (bundle in bundles) {
            if (bundle.removeIf { it.index == targetIdx }) {
                updatePieceCount()
                return randomArrange(targetIdx)
            }
        }
        return null
    }

    // 뭉치 분해 탐색
    fun searchForGroupDisbandment() {
        visited = Array(lengthCnt) { BooleanArray(widthCnt) }
        bundles.clear()

        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                if (isCorrected[i][j] && !visited[i][j]) {
                    val set = HashSet<Piece>()
                    dfsForSearch(i, j, set)
                    if (set.size == 1) {
                        deletePiece(board[i][j].index)
                    } else {
                        bundles.add(set)
                    }
                }
            }
        }
    }

    // DFS 탐색
    private fun dfsForSearch(r: Int, c: Int, set: MutableSet<Piece>) {
        visited[r][c] = true
        set.add(board[r][c])

        for (i in 0..3) {
            val nr = r + dx[i]
            val nc = c + dy[i]
            if (nr in 0 until lengthCnt && nc in 0 until widthCnt) {
                if (isCorrected[nr][nc] && !visited[nr][nc]) {
                    dfsForSearch(nr, nc, set)
                }
            }
        }
    }

    // 조각 랜덤 배치
    private fun randomArrange(pieceIdx: Int): DoubleArray {
        val (i, j) = idxToCoordinate[pieceIdx]!!
        val x = Random().nextInt(CANVAS_WIDTH).toDouble()
        val y = Random().nextInt(CANVAS_LENGTH).toDouble()
        board[i][j].position_x = x
        board[i][j].position_y = y
        return doubleArrayOf(x, y)
    }

    // 디버그용 출력
    fun printBoard() {
        println("---------------------------------------")
        println("총 조각 : ${widthCnt * lengthCnt}")
        println("맞춘 조각 : $correctedCount")
        println("진행률 : ${(correctedCount.toDouble() / (widthCnt * lengthCnt) * 100)}%")
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
        println("---------------------------------------")
    }

    // 최대공약수 계산 (사용되지 않음)
    fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }
}
