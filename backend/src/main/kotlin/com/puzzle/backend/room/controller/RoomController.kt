package com.puzzle.backend.room.controller

import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.response.RoomIdResponse
import com.puzzle.backend.room.dto.response.RoomListResponse
import com.puzzle.backend.room.dto.response.WaitingRoomResponse
import com.puzzle.backend.room.service.RoomService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val roomService: RoomService,
) {
    // 방 생성
    @PostMapping("")
    fun createRoom(
        @RequestBody request: CreateRoomRequest,
    ): ResponseEntity<RoomIdResponse> {
        val newRoom = roomService.createRoom(request)
        return ResponseEntity.ok(newRoom)
    }

    // 방 리스트 조회
    @GetMapping("")
    fun getRoomList(): ResponseEntity<List<RoomListResponse>> {
        val response = roomService.getRoomList()
        return ResponseEntity.ok(response)
    }

    // 방 입장
    @PostMapping("/{roomId}")
    fun entranceRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<RoomIdResponse> {
        val response = roomService.entranceRoom(roomId)
        return ResponseEntity.ok(response)
    }

    // 방 정보 조회
    @GetMapping("/{roomId}")
    fun getRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<WaitingRoomResponse> {
        val room = roomService.getRoom(roomId)
        return ResponseEntity.ok(room)
    }

    // 방 나가기
    @PostMapping("/{roomId}/exit")
    fun exitRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<String> {
        roomService.exitRoom(roomId)
        return ResponseEntity.ok("방 나가기 성공")
    }
}
