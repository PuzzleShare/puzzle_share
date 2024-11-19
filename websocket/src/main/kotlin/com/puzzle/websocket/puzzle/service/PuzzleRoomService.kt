package com.puzzle.websocket.puzzle.service

import com.puzzle.websocket.puzzle.dto.request.PlayerRequest

interface PuzzleRoomService {
    fun enterRoom(roomId: String, playerRequest: PlayerRequest)

    fun leaveRoom(roomId: String, playerRequest: PlayerRequest)

    fun moveTeam(roomId: String, playerRequest: PlayerRequest)

    fun gameStart(roomId: String, playerRequest: PlayerRequest)
}
