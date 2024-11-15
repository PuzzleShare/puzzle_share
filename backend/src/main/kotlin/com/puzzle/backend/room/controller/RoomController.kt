package com.puzzle.backend.room.controller

import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.response.RoomIdResponse
import com.puzzle.backend.room.dto.response.RoomListResponse
import com.puzzle.backend.room.dto.response.WaitingRoomResponse
import com.puzzle.backend.room.service.RoomService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("")
    fun getRoomList(
        @RequestParam(defaultValue = "0") page: Int,
    ): ResponseEntity<Page<RoomListResponse>> {
        val pageable: Pageable = PageRequest.of(page, 10)
        val rooms = roomService.getRoomList(pageable)
        return ResponseEntity.ok(rooms)
    }

    // 방 정보 조회
    @GetMapping("/{roomId}")
    fun getRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<WaitingRoomResponse> {
        val room = roomService.getRoom(roomId)
        return ResponseEntity.ok(room)
    }
}
