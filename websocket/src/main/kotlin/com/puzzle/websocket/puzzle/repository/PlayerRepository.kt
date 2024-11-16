package com.puzzle.websocket.puzzle.repository

import com.puzzle.websocket.puzzle.domain.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : CrudRepository<Player, Long>
