package com.puzzle.websocket.puzzle.repository

import com.puzzle.websocket.puzzle.domain.PuzzleRoom
import org.springframework.stereotype.Repository
import org.springframework.data.repository.CrudRepository

@Repository
interface PuzzleRoomRepository : CrudRepository<PuzzleRoom, String>