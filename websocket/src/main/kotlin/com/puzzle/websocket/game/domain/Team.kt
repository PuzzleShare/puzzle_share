package com.puzzle.websocket.game.domain

data class Team(
    var players: MutableList<User>,
){
    fun getPlayer(id: String): User? {
        return players.find { it.id==id }
    }

    fun isIn(id: String): Boolean{
        return players.any {it.id==id}
    }

    fun isIn(user: User): Boolean {
        return players.contains(user)
    }

    fun addPlayer(user: User) {
        players.add(user)
    }

    fun deletePlayer(user: User) {
        players.removeIf { it.id == user.id }
    }
}
