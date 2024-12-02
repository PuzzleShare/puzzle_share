package com.puzzle.backend.record.dto

import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.record.domain.GameRecord
import java.time.LocalDateTime

data class GameRecordDto(
    // 게임 기록 ID
    val recordId: Long = 0,
    // 사용자 ID
    val userId: Long,
    val gameName: String? = null,
    // 게임 유형 (BATTLE, COOPERATION)
    val gameType: String,
    // 협동 모드: 참가자 ID
    val players: List<String>? = null,
    // 퍼즐 이미지 URL
    val puzzleImage: String,
    // 퍼즐 조각 수
    val totalPieceCount: Int,
    val myPercent: Double? = 0.0,
    // 게임 시간 (분)
    val durationInMinutes: Int,
    // 게임 종료 시간
    val playedAt: LocalDateTime,
    // JSON 형태의 동료 팀원 ID 리스트
    val teamMates: String? = null,
    // JSON 형태의 상대 팀원 ID 리스트 (협동 모드의 경우 NULL)
    val opponents: String? = null,
    val myTeam: String? = null,
    val gameStatus: String? = null,
) {
    fun toEntity(user: Users): GameRecord =
        GameRecord(
            user = user,
            gameName = this.gameName!!,
            gameType = this.gameType,
            players = this.players.toString(),
            puzzleImage = this.puzzleImage,
            totalPieceCount = this.totalPieceCount,
            durationInMinutes = this.durationInMinutes,
            playedAt = this.playedAt,
            teamMates = this.teamMates,
            opponents = this.opponents,
            myTeam = this.myTeam,
            myPercent = this.myPercent,
            gameStatus = this.gameStatus,
        )
}
