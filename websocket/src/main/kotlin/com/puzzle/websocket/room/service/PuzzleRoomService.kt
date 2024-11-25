package com.puzzle.websocket.room.service

import com.puzzle.websocket.room.dto.request.InviteRequest
import com.puzzle.websocket.room.dto.request.PlayerRequest

interface PuzzleRoomService {
    fun enterRoom(
        roomId: String,
        playerRequest: PlayerRequest,
    )

    fun leaveRoom(
        roomId: String,
        playerRequest: PlayerRequest,
    )

    fun moveTeam(
        roomId: String,
        playerRequest: PlayerRequest,
    )

    fun gameStart(
        roomId: String,
        playerRequest: PlayerRequest,
    )

    fun invitePlayerToRoom(
        roomId: String,
        playerRequest: InviteRequest,
    )
}
