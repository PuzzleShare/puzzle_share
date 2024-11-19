package com.puzzle.websocket.game.domain

data class Team(
    var players: MutableList<User>,
) {
    fun getPlayer(id: String): User? = players.find { it.id == id }

    fun isIn(id: String): Boolean = players.any { it.id == id }

    fun isIn(user: User): Boolean = players.contains(user)

    fun addPlayer(user: User) {
        players.add(user)
    }

    fun deletePlayer(user: User) {
        players.removeIf { it.id == user.id }
    }
}
