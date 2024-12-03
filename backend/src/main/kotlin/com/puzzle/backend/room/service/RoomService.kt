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

        val image = downloadImage(request.puzzleImage)
        room.imgWidth = image.width
        room.imgHeight = image.height

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
        val response = roomList.map { RoomListResponse.toResponse(it, getParticipantCount(it.roomId)) }
        return response
    }

    fun validatePuzzleImage(imageUrl: String): ImageResponse {
        if (!isValidImageUrl(imageUrl)) {
            throw ImageValidationException("이미지 url이 유효하지 않습니다.")
        }

        val image = downloadImage(imageUrl)
        val width = image.width
        val height = image.height

        if (!isValidImageSize(width, height)) {
            throw ImageValidationException("이미지 사이즈가 범위를 벗어났습니다. ( 100 - 2000 )")
        }

        if (!isAspectRatioValid(width, height)) {
            throw ImageValidationException("퍼즐을 만들기에 이미지 비율이 알맞지 않습니다.")
        }

        return calculatePuzzlePieces(width, height)
    }

    fun calculatePuzzlePieces(
        width: Int,
        height: Int,
    ): ImageResponse {
        val pieceSize = 40
        val initialWidthPieces = max(width / pieceSize, 1)
        val initialHeightPieces = max(height / pieceSize, 1)

        var newWidth = initialWidthPieces * pieceSize
        var newHeight = initialHeightPieces * pieceSize

        var scaleFactor = 1.0
        if (newWidth > 500 || newHeight > 500) {
            val widthScale = 500.0 / newWidth
            val heightScale = 500.0 / newHeight
            scaleFactor = min(widthScale, heightScale)
            newWidth = floor(newWidth * scaleFactor / pieceSize).toInt() * pieceSize
            newHeight = floor(newHeight * scaleFactor / pieceSize).toInt() * pieceSize
        }

        val puzzlePiece = (newWidth / pieceSize) * (newHeight / pieceSize)

        return ImageResponse(width = newWidth, length = newHeight, puzzlePiece = puzzlePiece)
    }

    private fun scaleDimensions(
        width: Int,
        height: Int,
    ): Pair<Int, Int> {
        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return width to height
        }

        val scaleFactor = min(MAX_IMAGE_DIMENSION / width.toDouble(), MAX_IMAGE_DIMENSION / height.toDouble())
        return (width * scaleFactor).toInt() to (height * scaleFactor).toInt()
    }

    private fun downloadImage(imageUrl: String): BufferedImage {
        val imageBytes = restTemplate.getForObject(URI.create(imageUrl), ByteArray::class.java)
            ?: throw ImageValidationException("이미지를 다운받을 수 없습니다.")
        return ImageIO.read(ByteArrayInputStream(imageBytes))
            ?: throw ImageValidationException("이미지 파일을 읽을 수 없습니다.")
    }

    private fun isValidImageSize(
        width: Int,
        height: Int,
    ): Boolean = width in MIN_WIDTH..MAX_WIDTH && height in MIN_HEIGHT..MAX_HEIGHT

    private fun isAspectRatioValid(
        width: Int,
        height: Int,
    ): Boolean {
        val aspectRatio = if (height >= width) height.toDouble() / width else width.toDouble() / height
        return aspectRatio in MIN_ASPECT_RATIO..MAX_ASPECT_RATIO
    }

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
        val room = findById(roomId)
        val participantCount = room.redPlayers.size + room.bluePlayers.size
        if (participantCount == 0) deleteRoom(roomId)
        return participantCount
    }
}
