package com.puzzle.backend.record.repository

import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.record.domain.GameRecord
import org.springframework.data.jpa.repository.JpaRepository

interface GameRecordRepository : JpaRepository<GameRecord, Long> {
    // 특정 유저의 모든 게임 전적 조회
    fun findByUser(user: Users): List<GameRecord>

    // 특정 유저의 특정 게임 유형 전적 조회
    fun findByUserAndGameType(
        user: Users,
        gameType: String,
    ): List<GameRecord>
}
