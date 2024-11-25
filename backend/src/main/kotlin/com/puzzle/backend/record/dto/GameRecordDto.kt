package com.puzzle.backend.record.dto

import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.record.domain.GameRecord
import java.time.LocalDateTime

data class GameRecordDto(
    val recordId: Long = 0,              // 게임 기록 ID
    val userId: Long,                // 사용자 ID
    val gameType: String,            // 게임 유형 (BATTLE, COOPERATION)
    val players: List<Long>? = null, // 협동 모드: 참가자 ID
    val puzzleImage: String,         // 퍼즐 이미지 URL
    val totalPieceCount: Int,        // 퍼즐 조각 수
    val durationInMinutes: Int,      // 게임 시간 (분)
    val playedAt: LocalDateTime,     // 게임 종료 시간
    val teamMates: String? = null,           // JSON 형태의 동료 팀원 ID 리스트
    val opponents: String? = null,   // JSON 형태의 상대 팀원 ID 리스트 (협동 모드의 경우 NULL)
    val myTeam: String? = null,
    val gameStatus: String? = null,
) {
    fun toEntity(user: Users): GameRecord {
        return GameRecord(
            user = user,
            gameType = this.gameType,
            players = this.players.toString(),
            puzzleImage = this.puzzleImage,
            totalPieceCount = this.totalPieceCount,
            durationInMinutes = this.durationInMinutes,
            playedAt = this.playedAt,
            teamMates = this.teamMates,
            opponents = this.opponents,
            myTeam = this.myTeam,
            gameStatus = this.gameStatus,
        )
    }
}
