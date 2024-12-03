package com.puzzle.websocket.game.domain

import java.io.Serializable
import java.util.LinkedList
import java.util.Queue
import java.util.Random

class PuzzleBoard : Serializable {
    var picture: Picture? = null // 퍼즐에 쓰이는 사진
    var idxToCoordinate: MutableMap<Int, List<Int>> = mutableMapOf()

    // 조각들이 들어있는 2차원 배열
    var board: MutableList<MutableList<Piece>> = mutableListOf()
    var pieceSize: Int = 0 // 조각 크기
    var widthCnt: Int = 0 // 가로 조각 개수
    var lengthCnt: Int = 0 // 세로 조각 개수
    var totalBundles: Int = 0
    var totalEdges: Int = 0
    var connectedEdges: Int = 0
    private val canvasWidth = 1000
    private val canvasLength = 750

    // 조합된 퍼즐 뭉탱이들
    var bundles = hashMapOf<Int, MutableSet<Piece>>()
    lateinit var isCorrected: MutableList<MutableList<Boolean>> // 조합된 퍼즐인지 확인하는 2차원 배열
    var correctedCount: Int = 0 // 현재까지 맞춘 개수
    var isCompleted: Boolean = false
        private set

    companion object {
        // 랜덤 타입 적용에 쓰일 인덱스 상수
        const val TOP = 0
        const val RIGHT = 1
        const val BOTTOM = 2
        const val LEFT = 3

        // 방향 이동을 위한 배열
        val dx = listOf(1, -1, 0, 0)
        val dy = listOf(0, 0, -1, 1)

        const val CANVAS_WIDTH = 1000
        const val CANVAS_LENGTH = 750
    }

    val inventory: Array<Int> = Array(8) { 0 }
    val itemPiece = hashMapOf<Int, Boolean>()
    var addedFrame = false

    // 퍼즐 판 초기화
    fun init(
        p: Picture,
        gameType: String,
    ): MutableList<MutableList<Piece>> {
        picture = p
        pieceSize = p.pieceSize
        widthCnt = p.widthPieceCnt
        lengthCnt = p.lengthPieceCnt
        totalBundles = widthCnt * lengthCnt
        totalEdges = (widthCnt * (lengthCnt - 1)) + (lengthCnt * (widthCnt - 1))
        board = MutableList(lengthCnt) { MutableList(widthCnt) { Piece(0) } }
        isCorrected = MutableList(lengthCnt * 2 - 1) { MutableList(widthCnt * 2 - 1) { false } }

        var cnt = 0

        // 고유 인덱스 할당 및 주변 퍼즐에 대한 고유 인덱스 정보 초기화
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                val piece = Piece(cnt)
                bundles[cnt] = mutableSetOf(piece)
                board[i][j] = piece
                idxToCoordinate[cnt] = mutableListOf(i, j)

                piece.correctIndex[0] = if (j + 1 < widthCnt) cnt + 1 else -1
                piece.correctIndex[1] = if (j - 1 >= 0) cnt - 1 else -1
                piece.correctIndex[2] = if (i - 1 >= 0) cnt - widthCnt else -1
                piece.correctIndex[3] = if (i + 1 < lengthCnt) cnt + widthCnt else -1

                cnt++
            }
        }

        // 퍼즐 생성 알고리즘 적용
        generatePuzzlePieces()

        correctedCount = 0

        inventory[0] = (Math.random() * 4).toInt() + 1
        inventory[1] = (Math.random() * 4).toInt() + 1
        inventory[2] = (Math.random() * 4).toInt() + 1
        val firstItenPieceIndex = (Math.random() * cnt).toInt()
        itemPiece[firstItenPieceIndex] = false
        itemPiece[(firstItenPieceIndex + cnt / 3) % cnt] = false
        itemPiece[(firstItenPieceIndex + cnt / 3) * 2 % cnt] = false

        return board
    }

    fun reload(
        p: Picture,
        board: MutableList<MutableList<Piece>>,
        isCorrected: MutableList<MutableList<Boolean>>,
        correctedCount: Int,
        connectedEdges: Int,
        idxToCoordinate: MutableMap<Int, List<Int>>,
    ): PuzzleBoard {
        picture = p
        pieceSize = p.pieceSize
        widthCnt = p.widthPieceCnt
        lengthCnt = p.lengthPieceCnt
        totalEdges = (widthCnt * (lengthCnt - 1)) + (lengthCnt * (widthCnt - 1))
        this.correctedCount = correctedCount
        this.connectedEdges = connectedEdges
        this.board = board
        this.isCorrected = isCorrected
        this.idxToCoordinate = idxToCoordinate

        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                val piece = board[i][j]
                val bundleNum = piece.bundleNum
                // 해당 bundleNum에 해당하는 Set을 가져오거나, 없으면 새로 생성
                val bundleSet = bundles.getOrPut(bundleNum) { mutableSetOf() }

                bundleSet.add(piece)
            }
        }

        return this
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
                val (x, y) = calculatePiecePosition(i, j)

                randomPiece.position_x = x
                randomPiece.position_y = y
            }
        }

        // 타입 변환 (-1, 1)
        for (i in 0 until lengthCnt) {
            for (j in 0 until widthCnt) {
                board[i][j].type =
                    board[i][j]
                        .type
                        .map {
                            when (it) {
                                1 -> -1
                                2 -> 1
                                else -> 0
                            }
                        }.toIntArray()
            }
        }
    }

    private fun calculatePiecePosition(
        i: Int,
        j: Int,
    ): Pair<Double, Double> {
        val x = (CANVAS_WIDTH / 2 - pieceSize / 2 + pieceSize * ((j * 2) + (i % 2)) - picture!!.imgWidth + 50).toDouble()
        val y = (CANVAS_LENGTH / 2 - pieceSize / 2 + pieceSize * i - picture!!.imgHeight / 2).toDouble()
        return Pair(x, y)
    }

    // 타입 반전 함수
    private fun invertType(type: Int): Int =
        when (type) {
            1 -> 2
            2 -> 1
            else -> randomType()
        }

    // 랜덤 타입 생성
    private fun randomType(): Int {
        return Random().nextInt(2) + 1 // 1 또는 2 반환
    }

    fun addPiece(pieceList: List<Int>) {
        val piece1 = indexToPiece(pieceList[0])
        val piece2 = indexToPiece(pieceList[1])
        if (piece1.bundleNum == piece2.bundleNum) {
            return
        }

        val (fromBundle, toBundle) = bundles[piece1.bundleNum]!! to bundles[piece2.bundleNum]!!
        val fromBundleNum = piece1.bundleNum
        val toBundleNum = piece2.bundleNum

        updateConnectedEdgeCount(fromBundle, toBundle)
        fromBundle.forEach { it.bundleNum = toBundleNum }
        toBundle.addAll(fromBundle)
        bundles.remove(fromBundleNum)

        isCompleted = bundles.size == 1 // && connectedEdges == totalEdges
    }

    private fun indexToPiece(index: Int): Piece {
        val (i, j) = idxToCoordinate[index]!!
        return board[i][j]
    }

    private fun updateConnectedEdgeCount(
        bundleLarge: MutableSet<Piece>,
        bundleSmall: MutableSet<Piece>,
    ) {
        for (piece in bundleSmall) {
            for (correctIndex in piece.correctIndex) {
                if (correctIndex == -1) {
                    continue
                }
                val (x, y) = correctedCoordinate(piece.index, correctIndex)
                if (!isCorrected[x][y] && correctIndex in bundleLarge.map { it.index }) {
                    isCorrected[x][y] = true
                    connectedEdges++
                }
            }
        }
    }

    private fun correctedCoordinate(
        piece1: Int,
        piece2: Int,
    ): Pair<Int, Int> {
        val (x1, y1) = idxToCoordinate[piece1]!!
        val (x2, y2) = idxToCoordinate[piece2]!!
        return Pair(x1 + x2, y1 + y2)
    }

    fun calculateMixedProgress(): Double {
        val bundleProgress = (totalBundles - bundles.size) * 100.0 / (totalBundles - 1)
        // val connectedProgress = connectedEdges * 100.0 / totalEdges
        return bundleProgress
    }

    fun randomPosition(piece: Piece) {
        piece.position_x = Math.random() * canvasWidth
        piece.position_y = Math.random() * canvasLength
    }

    fun deletePiece(
        bundleKey: Int,
        piece: Piece,
    ) {
        if (bundleKey in bundles) {
            bundles[bundleKey]!!.remove(piece)
            piece.correctIndex.filter { it != -1 }.forEach {
                val (x, y) = correctedCoordinate(piece.index, it)
                if (isCorrected[x][y]) {
                    isCorrected[x][y] = false
                    connectedEdges--
                }
            }
        }
    }

    fun bundleSplit(bundleKey: Int) {
        val oldBundle = bundles[bundleKey]!!
        val indexToPiece = mutableMapOf<Int, Piece>()
        oldBundle.forEach { indexToPiece[it.index] = it }

        var newBundle = mutableSetOf<Piece>()
        val visit = mutableSetOf<Int>()
        val q: Queue<Piece> = LinkedList()

        for (piece in oldBundle) {
            if (piece.index in visit) {
                continue
            }

            q.add(piece)
            newBundle.add(piece)
            while (q.isNotEmpty()) {
                val poll = q.poll()
                for (i in poll.correctIndex) {
                    if (i == -1) continue
                    if (i in visit) continue
                    if (i !in indexToPiece) continue
                    q.add(indexToPiece[i])
                    visit.add(i)
                    newBundle.add(indexToPiece[i]!!)
                }
            }
            bundles[piece.index] = newBundle
            newBundle.forEach { it.bundleNum = piece.index }
            newBundle = mutableSetOf()
        }
    }

    fun getCanvasCenter(): Pair<Double, Double> = CANVAS_WIDTH / 2.0 to CANVAS_LENGTH / 2.0

    fun addItem(itemIdx: Int) {
        for (i in 0 until inventory.size) {
            if (inventory[i] == 0) {
                inventory[i] = itemIdx
                break
            }
        }
    }
}
