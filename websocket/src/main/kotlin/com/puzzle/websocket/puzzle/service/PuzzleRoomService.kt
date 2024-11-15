package com.puzzle.websocket.puzzle.service

interface PuzzleRoomService {
    fun enterRoom(roomId: String)

    fun leaveRoom(roomId: String)

    fun moveTeam()

    fun gameStart()
}
