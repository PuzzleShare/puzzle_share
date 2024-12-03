package com.puzzle.backend.room.service

import com.puzzle.backend.common.exception.custom.ImageValidationException
import com.puzzle.backend.room.domain.Room
import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.request.PlayerRequest
import com.puzzle.backend.room.dto.response.ImageResponse
import com.puzzle.backend.room.dto.response.RoomIdResponse
import com.puzzle.backend.room.dto.response.RoomListResponse
import com.puzzle.backend.room.dto.response.WaitingRoomResponse
import com.puzzle.backend.room.repository.RoomRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.net.URI
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val MIN_ASPECT_RATIO = 0.5
private const val MAX_ASPECT_RATIO = 2.0
private const val MIN_WIDTH = 100
private const val MAX_WIDTH = 2000
private const val MIN_HEIGHT = 100
private const val MAX_HEIGHT = 2000
private const val PIECE_SIZE = 40
private const val MAX_IMAGE_DIMENSION = 500

@Service
class RoomService(
    private val roomRepository: RoomRepository,
) {
    private val restTemplate = RestTemplate()

    init {
        ImageIO.scanForPlugins()
    }

    fun createRoom(request: CreateRoomRequest): RoomIdResponse {
        val room = request.toRoom()
        val player = PlayerRequest(request.playerId, request.playerImage, request.playerName)

        room.imgWidth = request.width
        room.imgLength = request.height

        room.bluePlayers.add(player)
        room.updateMaster(player)
        roomRepository.save(room)

        return RoomIdResponse(room.roomId)
    }

    fun getRoomList(): List<RoomListResponse> {
        val roomList = roomRepository
            .findAll()
            .filterNotNull()
            .sortedWith(
                compareBy<Room> { it.roomStatus != "WAITING" }
                    .thenByDescending { it.createdAt },
            ) // 방 이름으로 추가 정렬 필요시 사용
        val response = roomList
            .map { RoomListResponse.toResponse(it, getParticipantCount(it.roomId)) }
            .filter { it.nowPlayers != 0 }
        return response
    }

    fun validatePuzzleImage(imageUrl: String): ImageResponse {
        if (!isValidImageUrl(imageUrl)) {
            throw ImageValidationException("이미지 url이 유효하지 않습니다.")
        }

        try {
            // 2. 이미지 다운로드 시도
            val imageBytes: ByteArray? = restTemplate.getForObject(URI.create(imageUrl), ByteArray::class.java)
            if (imageBytes == null) {
                throw ImageValidationException("이미지를 다운받을 수 없습니다.")
            }

            // 3. 이미지 파일 읽기
            val image: BufferedImage? = ImageIO.read(ByteArrayInputStream(imageBytes))
            print(image.toString())
            if (image == null) {
                throw ImageValidationException("이미지를 다운받을 수 없습니다.")
            }
            val width = image.width
            val length = image.height
            val pieceSize = 40
// 초기 퍼즐 조각 수 계산 (내림)
            var initialWidthPieces: Int = width / pieceSize
            var initialLengthPieces: Int = length / pieceSize

            var widthPieceCnt: Int = 0
            var lengthPieceCnt: Int = 0
            var imgWidth: Int = 0
            var imgHeight: Int = 0

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
            // 원본 이미지 비율 계산
            val originalAspectRatio = if (length >= width) {
                length.toDouble() / width
            } else {
                width.toDouble() / length
            }

            if (!isValidImageSize(width, length)) {
                throw ImageValidationException("이미지 크기가 맞지 않습니다.")
            }
            // 비율 검증
            if (originalAspectRatio < MIN_ASPECT_RATIO || originalAspectRatio > MAX_ASPECT_RATIO) {
                throw ImageValidationException(
                    "이미지 비율이 허용 범위(${MIN_ASPECT_RATIO}~${MAX_ASPECT_RATIO})를 벗어났습니다. 현재 비율: $originalAspectRatio",
                )
            }

            val puzzlePiece = (widthPieceCnt * lengthPieceCnt)

            return ImageResponse(width = imgWidth, length = imgHeight, puzzlePiece = puzzlePiece)
        } catch (e: Exception) {
            // 로그를 남기고 false 반환 (선택 사항)
            println("printStackTrace")
            e.printStackTrace()
            throw e
        }
    }

    fun calculatePuzzlePieces(
        width: Int,
        height: Int,
    ): ImageResponse {
        val pieceSize = 40
        var initialWidthPieces: Int = width / pieceSize
        var initialLengthPieces: Int = height / pieceSize

        var widthPieceCnt: Int = 0
        var lengthPieceCnt: Int = 0
        var imgWidth: Int = 0
        var imgHeight: Int = 0

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
        // 원본 이미지 비율 계산
        val originalAspectRatio = if (height >= width) {
            height.toDouble() / width
        } else {
            width.toDouble() / height
        }
        val puzzlePiece = (widthPieceCnt * lengthPieceCnt)

        return ImageResponse(width = imgWidth, length = imgHeight, puzzlePiece = puzzlePiece)
    }

    private fun isValidImageSize(
        width: Int,
        height: Int,
    ): Boolean = width in MIN_WIDTH..MAX_WIDTH && height in MIN_HEIGHT..MAX_HEIGHT

    private fun isValidImageUrl(url: String): Boolean =
        try {
            val path = URI.create(url).path.lowercase()
            path.endsWith(".jpeg") ||
                path.endsWith(".jpg") ||
                path.endsWith(".png") ||
                path.endsWith(".gif") ||
                path.endsWith(".webp") ||
                path.endsWith(".bmp")
        } catch (e: Exception) {
            false
        }

    fun getRoom(roomId: String): WaitingRoomResponse {
        val room = findById(roomId)
        return WaitingRoomResponse.toResponse(room, getParticipantCount(roomId))
    }

    fun deleteRoom(roomId: String) {
        roomRepository.deleteById(roomId)
    }

    private fun findById(roomId: String): Room = roomRepository.findById(roomId).orElseThrow()

    private fun getParticipantCount(roomId: String): Int {
        val room = roomRepository.findById(roomId).getOrNull() ?: return 0
        val participantCount = room.redPlayers.size + room.bluePlayers.size
        return participantCount
    }
}
