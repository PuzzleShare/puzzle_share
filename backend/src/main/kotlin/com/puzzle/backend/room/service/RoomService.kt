package com.puzzle.backend.room.service

import com.puzzle.backend.common.exception.custom.RoomFullException
import com.puzzle.backend.common.status.RoomRole
import com.puzzle.backend.room.domain.Player
import com.puzzle.backend.room.domain.Room
import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.response.RoomIdResponse
import com.puzzle.backend.room.dto.response.RoomListResponse
import com.puzzle.backend.room.dto.response.WaitingRoomResponse
import com.puzzle.backend.room.repository.PlayerRepository
import com.puzzle.backend.room.repository.RoomRepository
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
) {
    fun createRoom(request: CreateRoomRequest): RoomIdResponse {
        val userId = 1L // TODO: 현재 사용자
        val room = request.toRoom()
        room.redPlayers.add(userId)
        roomRepository.save(room)

        val player = Player(userId, RoomRole.ROOM_MASTER, room.roomId)
        playerRepository.save(player)

        val response = RoomIdResponse(room.roomId)
        return response
    }

    fun getRoomList(): List<RoomListResponse> {
        val roomList = roomRepository.findAll().toList()
        val response = roomList.map { RoomListResponse.toResponse(it, getParticipantCount(it.roomId)) }
        return response
    }

    fun findById(roomId: String): Room = roomRepository.findById(roomId).orElseThrow()

    fun getRoom(roomId: String): WaitingRoomResponse {
        val room = findById(roomId)
        val response = WaitingRoomResponse.toResponse(room, getParticipantCount(roomId))
        return response
    }

    fun entranceRoom(roomId: String): RoomIdResponse {
        val userId = 2L // TODO: 현재 사용자
        val room = findById(roomId)
        val playerCount = room.bluePlayers.count() + room.redPlayers.count()
        if (playerCount >= room.maxPlayers) {
            throw RoomFullException("방이 가득 찼습니다.")
        }
        if (room.redPlayers.count() <= room.bluePlayers.count()) {
            room.redPlayers.add(userId)
        } else {
            room.bluePlayers.add(userId)
        }
        roomRepository.save(room)

        val player = Player(userId, RoomRole.USER, room.roomId)
        playerRepository.save(player)

        val response = RoomIdResponse(room.roomId)
        return response
    }

    fun exitRoom(roomId: String) {
        val userId = 2L // TODO: 현재 사용자
        val room = findById(roomId)
        room.redPlayers.remove(userId)
        room.bluePlayers.remove(userId)
        roomRepository.save(room)
        playerRepository.deleteById(userId)
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
