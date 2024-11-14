package com.puzzle.backend.room.repository

import com.puzzle.backend.room.domain.Room
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomRepository : CrudRepository<Room, String>
