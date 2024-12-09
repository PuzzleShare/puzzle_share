package com.puzzle.websocket.game.dto.response

import com.puzzle.websocket.room.dto.request.PlayerRequest

class PointerMoveDTO (
    val playerId: Long,
    val playerName: String,
    var x: Int,
    var y: Int,
    val team: String,
    val color: String,
){
    companion object{
        fun of(
            playerRequest: PlayerRequest,
            team: String,
            color: String,
        ):PointerMoveDTO{
            return PointerMoveDTO(
                team = team,
                x = 0,
                y = 0,
                color = color,
                playerId = playerRequest.playerId!!,
                playerName = playerRequest.playerName!!,
            )
        }
    }
}