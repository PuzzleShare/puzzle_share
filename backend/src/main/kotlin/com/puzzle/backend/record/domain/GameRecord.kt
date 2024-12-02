package com.puzzle.backend.record.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.record.dto.GameRecordDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "game_records")
data class GameRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val recordId: Long = 0L,
    @Column(nullable = false, name =  "game_id")
    val gameId: String? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: Users,
    @Column(nullable = false)
    val gameName: String,
    @Column(nullable = false)
    val gameType: String,
    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    val players: String? = null,
    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    val teamMates: String?,
    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    val opponents: String? = null,
    @Column(nullable = true)
    val myTeam: String?,
    @Column(nullable = true)
    val gameStatus: String?,
    @Column(nullable = false)
    val myPercent: Double?,
    @Column(nullable = false)
    val puzzleImage: String,
    @Column(nullable = false)
    val totalPieceCount: Int,
    @Column(nullable = false)
    val durationInMinutes: Int,
    @Column(nullable = false)
    val playedAt: LocalDateTime,
    @Column(nullable = false)
    val battleTimer: Int,
) {
    fun toDto(): GameRecordDto {
        val objectMapper = jacksonObjectMapper()

        return GameRecordDto(
            recordId = this.recordId,
            gameId = this.gameId.toString(),
            userId = this.user.userId,
            gameName = this.gameName,
            gameType = this.gameType,
            // JSON -> List<Long>
            players = this.players?.let { objectMapper.readValue<List<String>>(it) },
            puzzleImage = this.puzzleImage,
            totalPieceCount = this.totalPieceCount,
            durationInMinutes = this.durationInMinutes,
            playedAt = this.playedAt,
            // 이미 JSON 문자열이므로 그대로 전달
            teamMates = this.teamMates,
            myPercent = this.myPercent,
            // 이미 JSON 문자열이므로 그대로 전달
            opponents = this.opponents,
            myTeam = this.myTeam,
            gameStatus = this.gameStatus,
            battleTimer = this.battleTimer,
        )
    }
}
