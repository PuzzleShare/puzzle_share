package com.puzzle.websocket.common.exception.custom

class NoneMasterException(
    message: String = "Your not ROOM MASTER",
) : RuntimeException(message)
