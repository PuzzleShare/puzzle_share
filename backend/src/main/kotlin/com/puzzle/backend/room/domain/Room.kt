package com.puzzle.backend.room.domain

import java.io.Serializable

data class Room(
    val roomId: String, // 방 ID
    val roomName: String, // 방 이름
    val gameMode: String,
    val puzzlePiece: Int,
    val maxPlayers: Int, // 최대 참가자 수
    val currentPlayers: List<String> = listOf(), // 현재 참가자 목록
) : Serializable
