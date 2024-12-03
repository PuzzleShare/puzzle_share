package com.puzzle.websocket.game.domain

import java.io.Serializable
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

data class Picture(
    var id: Long? = null,
    var name: String? = null,
    val width: Int,
    val length: Int,
    val pieceSize: Int,
    var imgWidth: Int = 0,
    var imgHeight: Int = 0,
    var widthPieceCnt: Int = 0,
    var lengthPieceCnt: Int = 0,
    var encodedString: String? = null,
) : Serializable {
    init {
        // 초기 퍼즐 조각 수 계산 (내림)
        var initialWidthPieces: Int = width / pieceSize
        var initialLengthPieces: Int = length / pieceSize

        // 최소 1개의 조각은 필요
        initialWidthPieces = max(initialWidthPieces.toDouble(), 1.0).toInt()
        initialLengthPieces = max(initialLengthPieces.toDouble(), 1.0).toInt()

        // 초기 새로운 너비와 높이 계산
        var initialNewWidth = initialWidthPieces * pieceSize
        var initialNewLength = initialLengthPieces * pieceSize

        var scaleFactor = 1.0
        if (initialNewWidth > 500 || initialNewLength > 500) {
            // 너비와 높이 중 큰 비율을 찾아 스케일 팩터 계산
            val widthScale = 500.0 / initialNewWidth
            val lengthScale = 500.0 / initialNewLength
            scaleFactor = min(widthScale, lengthScale)

            // 스케일 팩터를 적용하여 새로운 크기 계산
            val scaledWidth = initialNewWidth * scaleFactor
            val scaledLength = initialNewLength * scaleFactor

            // 퍼즐 조각 수 재계산 (내림)
            initialWidthPieces = floor(scaledWidth / pieceSize).toInt()
            initialLengthPieces = floor(scaledLength / pieceSize).toInt()

            // 최소 1개의 조각은 필요
            widthPieceCnt = max(initialWidthPieces.toDouble(), 1.0).toInt()
            lengthPieceCnt = max(initialLengthPieces.toDouble(), 1.0).toInt()

            // 최종 새로운 너비와 높이 계산
            imgWidth = widthPieceCnt * pieceSize
            imgHeight = lengthPieceCnt * pieceSize
        } else {
            // 스케일링이 필요 없을 경우
            widthPieceCnt = initialWidthPieces
            lengthPieceCnt = initialLengthPieces
            imgWidth = initialNewWidth
            imgHeight = initialNewLength
        }
    }

    companion object {
        private val levelSize: Map<Int, Int> = mapOf(1 to 400, 2 to 500, 3 to 600)

        // 새로운 이미지 정보를 받아 생성하는 create 메서드 추가
        fun create(
            pieceSize: Int,
            imageName: String,
            encodedString: String,
            width: Int,
            length: Int,
        ): Picture {
            try {
                return Picture(
                    name = imageName,
                    width = width,
                    length = length,
                    pieceSize = pieceSize,
                    encodedString = encodedString,
                )
            } catch (e: Exception) {
                // 기본 이미지 설정 또는 예외 처리
                return Picture(
                    name = "짱구.jpg",
                    width = 1000,
                    length = 551,
                    pieceSize = pieceSize,
                    encodedString =
                        "https://i.namu.wiki/i/1zQlFS0_ZoofiPI4-mcmXA8zXHEcgFiAbHcnjGr7RAEyjwMHvDbrbsc8ekjZ5iWMGyzJrGl96Fv5ZIgm6YR_nA.webp",
                )
            }
        }
    }
}
