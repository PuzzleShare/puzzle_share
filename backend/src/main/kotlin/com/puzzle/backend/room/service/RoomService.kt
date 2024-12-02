package com.puzzle.backend.room.service

import com.puzzle.backend.room.domain.Room
import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.request.PlayerRequest
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
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val MIN_ASPECT_RATIO = 0.5
private const val MAX_ASPECT_RATIO = 2.0
private const val MIN_WIDTH = 100
private const val MAX_WIDTH = 2000
private const val MIN_HEIGHT = 100
private const val MAX_HEIGHT = 2000

@Service
class RoomService(
    private val roomRepository: RoomRepository,
) {
    init {
        // WebP 플러그인 등록 (필요한 경우)
        ImageIO.scanForPlugins()
    }

    fun createRoom(request: CreateRoomRequest): RoomIdResponse {
        val room = request.toRoom()
        val player = PlayerRequest(request.playerId, request.playerImage, request.playerName)

        room.bluePlayers.add(player)
        room.updateMaster(player)
        roomRepository.save(room)

        val response = RoomIdResponse(room.roomId)
        return response
    }

    fun getRoomList(): List<RoomListResponse> {
        val roomList = roomRepository
            .findAll()
            .filterNotNull()
            .sortedWith(
                compareBy<Room> { it.roomStatus != "WAITING" }
                    .thenBy { it.createdAt },
            ) // 방 이름으로 추가 정렬 필요시 사용
        val response = roomList.map { RoomListResponse.toResponse(it, getParticipantCount(it.roomId)) }
        return response
    }

    fun findById(roomId: String): Room = roomRepository.findById(roomId).orElseThrow()

    fun getRoom(roomId: String): WaitingRoomResponse {
        val room = findById(roomId)
        val response = WaitingRoomResponse.toResponse(room, getParticipantCount(roomId))
        return response
    }

    fun deleteRoom(roomId: String) {
        roomRepository.deleteById(roomId)
    }

    fun getParticipantCount(roomId: String): Int {
        val room = findById(roomId)
        val participantCount = room.redPlayers.count() + room.bluePlayers.count()
        if (participantCount == 0) {
            deleteRoom(roomId)
        }
        return participantCount
    }

    private val restTemplate = RestTemplate()
    // 비율 검증을 위한 최소 및 최대 비율 정의

    fun isPuzzleImageValid(imageUrl: String): Int {
        // 1. URL 형식 및 이미지 확장자 검사
        if (!isValidImageUrl(imageUrl)) {
            return 0
        }

        try {
            // 2. 이미지 다운로드 시도
            val imageBytes: ByteArray? = restTemplate.getForObject(URI.create(imageUrl), ByteArray::class.java)
            if (imageBytes == null) {
                return 0
            }

            // 3. 이미지 파일 읽기
            val image: BufferedImage? = ImageIO.read(ByteArrayInputStream(imageBytes))
            print(image.toString())
            if (image == null) {
                return 0
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
                return 0
            }
            // 비율 검증
            if (originalAspectRatio < MIN_ASPECT_RATIO || originalAspectRatio > MAX_ASPECT_RATIO) {
                throw IllegalArgumentException(
                    "이미지 비율이 허용 범위(${MIN_ASPECT_RATIO}~${MAX_ASPECT_RATIO})를 벗어났습니다. 현재 비율: $originalAspectRatio",
                )
            }

            return widthPieceCnt * lengthPieceCnt
        } catch (e: Exception) {
            // 로그를 남기고 false 반환 (선택 사항)
            println("printStackTrace")
            e.printStackTrace()
            return 0
        }
    }

    private fun isValidImageSize(
        width: Int,
        height: Int,
    ): Boolean = width in MIN_WIDTH..MAX_WIDTH && height in MIN_HEIGHT..MAX_HEIGHT

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
