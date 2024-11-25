package com.puzzle.websocket.game.domain

object ListStringUtils {
    // List<Any>를 ','로 구분된 String으로 변환
    fun listToString(list: List<Any>): String = list.joinToString(",") { it.toString() }

    fun stringToList(input: String): List<Any> =
        if (input.isBlank()) {
            emptyList()
        } else {
            input.split(",").map { it.trim() }
        }
}
