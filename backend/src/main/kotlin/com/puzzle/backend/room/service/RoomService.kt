package com.puzzle.backend.room.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.puzzle.backend.room.domain.Room
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RoomService(
    private val redisService: RedisService,
) {
    // 방 생성
    fun createRoom(
        name: String,
        maxPlayers: Int,
    ): Room {
        // 고유한 방 ID 생성
        val roomId = UUID.randomUUID().toString()
        // Room 객체 생성
        val room =
            Room(
                roomId = roomId,
                roomName = name,
                gameMode = "normal",
                puzzlePiece = 100,
                maxPlayers = 8,
            )
        val json = ObjectMapper().writeValueAsString(room)
        // Redis에 Room 정보를 저장 (기본 TTL 설정 없이 저장)
        redisService.save("test", json)
        // 필요한 경우 TTL을 설정할 수 있습니다. (예: 1시간 후 만료)
        // redisService.saveWithExpiration("room:$roomId", room, 1, TimeUnit.HOURS)

        return room
    }

    // 방 정보 조회
    fun getRoom(roomId: String): Room? = redisService.find("room:$roomId") as? Room

    // 방 삭제
    fun deleteRoom(roomId: String): Boolean = redisService.delete("room:$roomId")
}
