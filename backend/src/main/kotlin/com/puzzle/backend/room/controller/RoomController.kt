package com.puzzle.backend.room.controller

import com.puzzle.backend.room.domain.Room
import com.puzzle.backend.room.service.RoomService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val roomService: RoomService,
) {
    // 방 생성 API
    @PostMapping("/")
    fun createRoom(
        @RequestParam name: String,
    ): ResponseEntity<Room> {
        val newRoom = roomService.createRoom(name)
        return ResponseEntity.ok(newRoom)
    }

    // 방 정보 조회 API
    @GetMapping("/{roomId}")
    fun getRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<Room?> {
        val room = roomService.getRoom(roomId)
        return if (room != null) {
            ResponseEntity.ok(room)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 방 삭제 API
    @DeleteMapping("/{roomId}")
    fun deleteRoom(
        @PathVariable roomId: String,
    ): ResponseEntity<Void> =
        if (roomService.deleteRoom(roomId)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
}
