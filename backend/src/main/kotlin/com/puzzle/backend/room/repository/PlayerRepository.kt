package com.puzzle.backend.room.repository

import com.puzzle.backend.room.domain.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : CrudRepository<Player, Long>
