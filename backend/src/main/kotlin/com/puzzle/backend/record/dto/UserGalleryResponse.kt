package com.puzzle.backend.record.dto

import com.puzzle.backend.record.domain.GameRecord
import java.time.LocalDateTime

data class UserGalleryResponse(
    val recordId: Long,
    val gameName: String,
    val puzzleImage: String,
    val totalPieceCount: Int,
    val durationInMinutes: Int,
    val playedAt: LocalDateTime,
    val teamMates: String?,
    val opponents: String?,
    val myTeam: String?,
    val battleTimer: Int = 0,
) {
    companion object {
        fun of(gameRecord: GameRecord): UserGalleryResponse =
            UserGalleryResponse(
                recordId = gameRecord.recordId,
                gameName = gameRecord.gameName,
                puzzleImage = gameRecord.puzzleImage,
                totalPieceCount = gameRecord.totalPieceCount,
                durationInMinutes = gameRecord.durationInMinutes,
                playedAt = gameRecord.playedAt,
                teamMates = gameRecord.teamMates,
                opponents = gameRecord.opponents,
                myTeam = gameRecord.myTeam,
                battleTimer = gameRecord.battleTimer,
            )
    }
}
