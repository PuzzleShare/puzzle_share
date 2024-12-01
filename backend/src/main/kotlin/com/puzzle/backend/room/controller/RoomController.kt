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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val roomService: RoomService,
) : RoomControllerSpec {
    // 방 생성
    @PostMapping("")
    override fun createRoom(
        @RequestBody request: CreateRoomRequest,
    ): ResponseEntity<RoomIdResponse> {
        if (request.gameMode.equals("battle", ignoreCase = true)) {
            // 1, 3, 5, 8, 10분
            val validTimers = listOf(60, 180, 300, 480, 600)
            if (request.battleTimer == null || request.battleTimer !in validTimers) {
                return ResponseEntity.badRequest().body(RoomIdResponse("Invalid battle timer"))
            }
        }

        val newRoom = roomService.createRoom(request)
        return ResponseEntity.ok(newRoom)
    }

    @GetMapping("")
    override fun getRoomList(): ResponseEntity<List<RoomListResponse>> {
        val rooms = roomService.getRoomList()
        return ResponseEntity.ok(rooms)
    }

    // 방 정보 조회
    @GetMapping("/{roomId}")
    override fun getRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<WaitingRoomResponse> {
        val room = roomService.getRoom(roomId)
        return ResponseEntity.ok(room)
    }

    @PostMapping("/image/dimensions")
    fun getImageDimensions(
        @RequestParam imageUrl: String,
    ): ResponseEntity<Int> {
        val response = roomService.isPuzzleImageValid(imageUrl)
        return ResponseEntity.ok(response)
    }
}
