package com.puzzle.websocket.room.repository

import com.puzzle.websocket.room.domain.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : CrudRepository<Player, Long>
