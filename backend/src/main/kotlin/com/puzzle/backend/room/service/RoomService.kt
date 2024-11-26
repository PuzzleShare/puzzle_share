package com.puzzle.backend.room.service

import com.puzzle.backend.room.domain.Room
import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.request.PlayerRequest
import com.puzzle.backend.room.dto.response.RoomIdResponse
import com.puzzle.backend.room.dto.response.RoomListResponse
import com.puzzle.backend.room.dto.response.WaitingRoomResponse
import com.puzzle.backend.room.repository.RoomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.net.URI
import javax.imageio.ImageIO

@Service
class RoomService(
    private val roomRepository: RoomRepository,
) {
    fun createRoom(request: CreateRoomRequest): RoomIdResponse {
        val room = request.toRoom(request.playerId)
        val player = PlayerRequest(request.playerId, request.playerImage!!, request.playerName!!)

        room.bluePlayers.add(player)
        room.updateMaster(player.playerId)
        roomRepository.save(room)

        val response = RoomIdResponse(room.roomId)
        return response
    }

    fun getRoomList(pageable: Pageable): Page<RoomListResponse> {
        val roomList = roomRepository.findAll().filterNotNull().toList()
        val start = pageable.pageNumber * pageable.pageSize
        val end = minOf(start + pageable.pageSize, roomList.size)

        val pagedRooms =
            roomList
                .subList(start, end)
                .map { RoomListResponse.toResponse(it, getParticipantCount(it.roomId)) }

        return PageImpl(pagedRooms, pageable, roomList.size.toLong())
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

    fun isPuzzleImageValid(imageUrl: String): Boolean {
        // 1. URL 형식 및 이미지 확장자 검사
        println(imageUrl)
        if (!isValidImageUrl(imageUrl)) {
            println("isValidImageUrl")
            return false
        }

        try {
            // 2. 이미지 다운로드 시도
            val imageBytes: ByteArray? = restTemplate.getForObject(URI.create(imageUrl), ByteArray::class.java)
            if (imageBytes == null) {
                println("imageBytes")
                return false
            }

            // 3. 이미지 파일 읽기
            val image: BufferedImage? = ImageIO.read(ByteArrayInputStream(imageBytes))
            print(image.toString())
            if (image == null) {
                println("image")
                return false
            }

            return true
        } catch (e: Exception) {
            // 로그를 남기고 false 반환 (선택 사항)
            println("printStackTrace")
            e.printStackTrace()
            return false
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
