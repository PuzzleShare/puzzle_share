package com.puzzle.backend.record.dto

data class UserGameRecordsResponse(
    val userId: Long,
    val userName: String,
    val userImage: String,
    val winRate: Double,
    val winCount: Int,
    val lossCount: Int,
    val drawCount: Int,
    val totalGames: Int,
    val records: List<GameRecordDto>,
    // 총 페이지 수
    val totalPages: Int,
    // 총 게임 기록 수
    val totalElements: Long,
    // 현재 페이지 번호
    val currentPage: Int,
)
