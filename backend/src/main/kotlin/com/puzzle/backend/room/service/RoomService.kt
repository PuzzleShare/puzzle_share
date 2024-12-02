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
                    .thenBy { it.roomName },
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

    fun isPuzzleImageValid(imageUrl: String): Boolean {
        // 1. URL 형식 및 이미지 확장자 검사
        if (!isValidImageUrl(imageUrl)) {
            return false
        }

        try {
            // 2. 이미지 다운로드 시도
            val imageBytes: ByteArray? = restTemplate.getForObject(URI.create(imageUrl), ByteArray::class.java)
            if (imageBytes == null) {
                return false
            }

            // 3. 이미지 파일 읽기
            val image: BufferedImage? = ImageIO.read(ByteArrayInputStream(imageBytes))
            print(image.toString())
            if (image == null) {
                return false
            }
            val width = image.width
            val height = image.height

            // 원본 이미지 비율 계산
            val originalAspectRatio = if (height >= width) {
                height.toDouble() / width
            } else {
                width.toDouble() / height
            }

            if (!isValidImageSize(width, height)) {
                return false
            }
            // 비율 검증
            if (originalAspectRatio < MIN_ASPECT_RATIO || originalAspectRatio > MAX_ASPECT_RATIO) {
                throw IllegalArgumentException(
                    "이미지 비율이 허용 범위(${MIN_ASPECT_RATIO}~${MAX_ASPECT_RATIO})를 벗어났습니다. 현재 비율: $originalAspectRatio",
                )
            }

            return true
        } catch (e: Exception) {
            // 로그를 남기고 false 반환 (선택 사항)
            println("printStackTrace")
            e.printStackTrace()
            return false
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
