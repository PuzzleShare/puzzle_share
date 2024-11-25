package com.puzzle.backend.record.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.record.dto.GameRecordDto
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_records")
data class GameRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val recordId: Long = 0L,          // 게임 기록 ID

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: Users,                 // 사용자 엔티티와 연관 관계

    @Column(nullable = false)
    val gameType: String,            // 게임 유형 (BATTLE, COOPERATION)

    @Lob
    @Column(nullable = true)
    val players: String? = null,     // JSON 형태로 저장된 협동 모드 참가자 ID 리스트

    @Lob
    @Column(nullable = true)
    val teamMates: String?,           // JSON 형태의 동료 팀원 ID 리스트

    @Lob
    @Column(nullable = true)
    val opponents: String? = null,   // JSON 형태의 상대 팀원 ID 리스트 (협동 모드의 경우 NULL)

    @Column(nullable = true)
    val myTeam: String?,         // 내 팀 (RED, BLUE, 또는 NULL)

    @Column(nullable = true)
    val gameStatus: String?,          // 승리 여부 (RED, BLUE, DRAW)

    @Column(nullable = false)
    val puzzleImage: String,         // 퍼즐 이미지 URL

    @Column(nullable = false)
    val totalPieceCount: Int,        // 퍼즐 조각 수

    @Column(nullable = false)
    val durationInMinutes: Int,      // 게임 시간 (분)

    @Column(nullable = false)
    val playedAt: LocalDateTime,     // 게임 종료 시간

)
{
    fun toDto(): GameRecordDto {
        val objectMapper = jacksonObjectMapper()

        return GameRecordDto(
            recordId = this.recordId,
            userId = this.user.userId,
            gameType = this.gameType,
            players = this.players?.let { objectMapper.readValue<List<Long>>(it) }, // JSON -> List<Long>
            puzzleImage = this.puzzleImage,
            totalPieceCount = this.totalPieceCount,
            durationInMinutes = this.durationInMinutes,
            playedAt = this.playedAt,
            teamMates = this.teamMates, // 이미 JSON 문자열이므로 그대로 전달
            opponents = this.opponents, // 이미 JSON 문자열이므로 그대로 전달
            myTeam = this.myTeam,
            gameStatus = this.gameStatus,
        )
    }
}
