package com.puzzle.websocket.puzzle.service

import com.puzzle.websocket.puzzle.dto.request.RoomIdRequest

interface PuzzleRoomService {
    fun enterRoom(roomId: RoomIdRequest)

    fun leaveRoom(roomId: RoomIdRequest)

    fun moveTeam(roomId: RoomIdRequest)

    fun gameStart(roomId: RoomIdRequest)
}
