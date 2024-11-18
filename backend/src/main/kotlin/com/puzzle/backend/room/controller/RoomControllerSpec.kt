package com.puzzle.backend.room.controller

import com.puzzle.backend.room.dto.request.CreateRoomRequest
import com.puzzle.backend.room.dto.response.RoomIdResponse
import com.puzzle.backend.room.dto.response.RoomListResponse
import com.puzzle.backend.room.dto.response.WaitingRoomResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity

@Tag(name = "2. Room API", description = "방 관련 API")
interface RoomControllerSpec {
    @Operation(
        summary = "방 생성",
        description = "새로운 방을 생성합니다.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "방 생성 요청 정보",
            required = true,
        ),
    )
    fun createRoom(request: CreateRoomRequest): ResponseEntity<RoomIdResponse>

    @Operation(
        summary = "방 목록 조회",
        description = "페이징 처리된 방 목록을 조회합니다.",
        parameters = [
            Parameter(
                name = "page",
                description = "조회할 페이지 번호 (0부터 시작)",
                required = false,
                example = "0",
            ),
        ],
    )
    fun getRoomList(page: Int): ResponseEntity<Page<RoomListResponse>>

    @Operation(
        summary = "방 정보 조회",
        description = "특정 방의 정보를 조회합니다.",
        parameters = [
            Parameter(
                name = "roomId",
                description = "조회할 방의 ID",
                required = true,
                example = "12345",
            ),
        ],
    )
    fun getRoom(roomId: String): ResponseEntity<WaitingRoomResponse>
}
