package com.puzzle.websocket.game.domain

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
) {
    private val levelSize: Map<Int, Int> = mapOf(1 to 400, 2 to 500, 3 to 600)

    init {
        val levelOneSize = levelSize[1]!!

        if (length >= width) {
            imgWidth = ((levelOneSize * width) / length / 100) * 100
            imgHeight = levelOneSize
        } else {
            imgWidth = levelOneSize
            imgHeight = ((levelOneSize * length) / width / 100) * 100
        }

        widthPieceCnt = (imgWidth / pieceSize.toDouble()).toInt()
        lengthPieceCnt = (imgHeight / pieceSize.toDouble()).toInt()
    }

    fun create(
        width: Int,
        length: Int,
        name: String,
        pieceSize: Int,
        encodedString: String,
    ): Picture =
        Picture(
            name = name,
            width = width,
            length = length,
            pieceSize = pieceSize,
            encodedString = encodedString,
        )

    companion object {
        // 새로운 이미지 정보를 받아 생성하는 create 메서드 추가
        fun create(
            width: Int,
            length: Int,
            pieceSize: Int,
            imageName: String,
            encodedString: String
        ): Picture
            = Picture(
                name = imageName,
                width = width,
                length = length,
                pieceSize = pieceSize,
                encodedString = encodedString
            )

        // 기존의 기본값으로 생성하는 create 메서드 유지
        fun create(): Picture
            = Picture(
                name = "짱구.jpg",
                width = 1000,
                length = 551,
                pieceSize = 40,
                encodedString = "짱구.jpg"
            )
    }
}
