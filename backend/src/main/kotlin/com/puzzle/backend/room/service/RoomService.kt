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

@Service
class RoomService(
    private val roomRepository: RoomRepository,
) {
    fun createRoom(request: CreateRoomRequest): RoomIdResponse {
        val room = request.toRoom(request.playerId)
        val player = PlayerRequest(request.playerId, request.playerImage, request.playerName)

        room.bluePlayers.add(player)
        room.updateMaster(player.playerId)
        roomRepository.save(room)

        val response = RoomIdResponse(room.roomId)
        return response
    }

    fun getRoomList(pageable: Pageable): Page<RoomListResponse> {
        val roomList = roomRepository.findAll().toList()
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
}
