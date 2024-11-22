package com.puzzle.backend.oauth.repository

import com.puzzle.backend.oauth.domain.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<Users, Long> {
    fun findBySocialTypeAndEmail(
        socialType: String,
        email: String,
    ): Users?
}
