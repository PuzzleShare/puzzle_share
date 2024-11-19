package com.puzzle.websocket.room.repository

import com.puzzle.websocket.puzzle.dto.request.PlayerRequest
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : CrudRepository<PlayerRequest, Long>
