package com.puzzle.websocket.game.domain

import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.Serializable
import java.net.URI
import javax.imageio.ImageIO

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
        val levelOneSize = Companion.levelSize[1]!!
        if (length >= width) {
            imgWidth = ((levelOneSize * width) / length / 100) * 100
            imgHeight = levelOneSize
            if (imgWidth == 0) {
                imgWidth = pieceSize
            }
        } else {
            imgWidth = levelOneSize
            imgHeight = ((levelOneSize * length) / width / 100) * 100
            if (imgHeight == 0) {
                imgHeight = pieceSize
            }
        }

        widthPieceCnt = (imgWidth / pieceSize.toDouble()).toInt()
        lengthPieceCnt = (imgHeight / pieceSize.toDouble()).toInt()
    }

    companion object {
        private val restTemplate = RestTemplate()
        private val levelSize: Map<Int, Int> = mapOf(1 to 400, 2 to 500, 3 to 600)

        // 새로운 이미지 정보를 받아 생성하는 create 메서드 추가
        fun create(
            pieceSize: Int,
            imageName: String,
            encodedString: String,
        ): Picture {
            try {
                val imageData = getImageDimensions(encodedString)
                return Picture(
                    name = imageName,
                    width = imageData.width,
                    length = imageData.height,
                    pieceSize = pieceSize,
                    encodedString = encodedString,
                )
            } catch (e: Exception) {
                return Picture(
                    name = "짱구.jpg",
                    width = 1000,
                    length = 551,
                    pieceSize = pieceSize,
                    encodedString = "https://i.namu.wiki/i/1zQlFS0_ZoofiPI4-mcmXA8zXHEcgFiAbHcnjGr7RAEyjwMHvDbrbsc8ekjZ5iWMGyzJrGl96Fv5ZIgm6YR_nA.webp",
                )
            }
        }

        fun getImageDimensions(imageUrl: String): ImageDimensionDto {
            // 이미지 URL 유효성 검사
            if (!isValidImageUrl(imageUrl)) {
                throw IllegalArgumentException("유효한 이미지 URL이 아닙니다.")
            }

            try {
                // 이미지 다운로드
                val imageBytes: ByteArray? = restTemplate.getForObject(URI.create(imageUrl), ByteArray::class.java)
                if (imageBytes == null) {
                    throw Exception("이미지 데이터를 다운로드할 수 없습니다.")
                }

                // 이미지 크기 추출
                val image: BufferedImage? = ImageIO.read(ByteArrayInputStream(imageBytes))
                if (image == null) {
                    throw Exception("이미지 파일을 읽을 수 없습니다.")
                }

                val width = image.width
                val height = image.height

                return ImageDimensionDto(width, height)
            } catch (e: Exception) {
                throw e
            }
        }

        private fun isValidImageUrl(url: String): Boolean =
            try {
                val uri = URI.create(url)
                val path = uri.path.lowercase()
                path.endsWith(".jpeg") ||
                    path.endsWith(".jpg") ||
                    path.endsWith(".png") ||
                    path.endsWith(".gif") ||
                    path.endsWith(".webp") ||
                    path.endsWith(".bmp")
            } catch (e: Exception) {
                false
            }
    }
}
