package com.puzzle.websocket.puzzle.repository

import com.puzzle.websocket.puzzle.domain.PuzzleRoom
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PuzzleRoomRepository : CrudRepository<PuzzleRoom, String>
