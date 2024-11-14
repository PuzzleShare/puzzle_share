package com.puzzle.websocket.puzzle.dto

// Team 클래스는 사용자들의 팀을 관리하는 역할을 한다.
// 팀은 여러 User 객체들을 포함하고 있으며,
// 팀에 사용자를 추가, 삭제하거나
// 특정 사용자가 팀에 속해 있는지 확인하는 기능을 제공
data class Team(
    // 팀에 속해 있는 사용자들을 저장하는 리스트
    // MutableList를 사용한 이유는
    // 팀 멤버를 동적으로 추가하거나 삭제할 수 있도록 하기 위함
    val players: MutableList<User>
) {
    // 전달된 user가 팀의 players 리스트에 포함되어 있는지 확인
    fun isIn(user: User): Boolean {
        return players.contains(user)
    }

    // 전달된 user를 팀의 players 리스트에 추가
    // 이때 추가하려는 사용자가 이미 팀에 속해 있는지 검사는 하지 않으므로,
    // 중복된 사용자가 추가되지 않도록 호출 시 확인하는 것이 좋다.
    fun addPlayer(user: User) {
        players.add(user)
    }

    // 전달된 user와 동일한 id를 가진 사용자를 팀에서 제거
    fun deletePlayer(user: User) {
        for (i in players.size - 1 downTo 0) {
            if (players[i].id == user.id) {
                players.removeAt(i)
                break
            }
        }
    }
}