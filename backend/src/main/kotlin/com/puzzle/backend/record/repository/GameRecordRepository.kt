package com.puzzle.backend.record.repository

import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.record.domain.GameRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface GameRecordRepository : JpaRepository<GameRecord, Long> {
    // 특정 유저의 모든 게임 전적 조회
    fun findByUser(user: Users): List<GameRecord>

    // 특정 유저의 특정 게임 유형 전적 조회
    fun findByUserAndGameType(
        user: Users,
        gameType: String,
    ): List<GameRecord>

    // 특정 사용자의 게임 기록을 페이징으로 가져오기
    fun findByUserOrderByPlayedAtDesc(
        user: Users,
        pageable: Pageable,
    ): Page<GameRecord>

    // 특정 게임 유형의 기록을 페이징 처리
    fun findByUserAndGameTypeOrderByPlayedAtDesc(
        user: Users,
        gameType: String,
        pageable: Pageable,
    ): Page<GameRecord>

    fun findGameRecordsByMyPercentAndUserOrderByPlayedAtDesc(
        myPercent: Double = 100.0,
        user: Users,

    ): List<GameRecord>
}
