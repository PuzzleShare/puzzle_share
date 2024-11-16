package com.puzzle.backend.common.oauth.repository

import com.puzzle.backend.common.oauth.domain.UserCache
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCacheRepository: CrudRepository<UserCache, Long>
