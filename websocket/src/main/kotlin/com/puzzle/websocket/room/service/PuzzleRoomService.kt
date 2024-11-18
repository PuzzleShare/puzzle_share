package com.puzzle.websocket.room.service

import com.puzzle.websocket.room.dto.request.RoomIdRequest

interface PuzzleRoomService {
    fun enterRoom(roomId: RoomIdRequest)

    fun leaveRoom(roomId: RoomIdRequest)

    fun moveTeam(roomId: RoomIdRequest)

    fun gameStart(roomId: RoomIdRequest)
}
