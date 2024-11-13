package com.puzzle.backend.common.oauth.repository

import com.puzzle.backend.common.oauth.domain.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository:JpaRepository<Users, Long>{
    fun findBySocialTypeAndEmail(socialType:String, email:String): Users?
}