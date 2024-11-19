package com.puzzle.websocket.room.repository

import com.puzzle.websocket.room.domain.PuzzleRoom
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PuzzleRoomRepository : CrudRepository<PuzzleRoom, String>
