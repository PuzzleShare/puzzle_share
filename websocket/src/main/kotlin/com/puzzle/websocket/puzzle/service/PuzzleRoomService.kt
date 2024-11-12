package com.puzzle.websocket.puzzle.service

interface PuzzleRoomService {
    fun enterRoom()

    fun leaveRoom()

    fun moveTeam()

    fun gameStart()
}
