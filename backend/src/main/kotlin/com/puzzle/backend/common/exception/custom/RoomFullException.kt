package com.puzzle.backend.common.exception.custom

class RoomFullException(
    message: String = "Room is full",
) : RuntimeException(message)
