package com.puzzle.backend.record.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import com.puzzle.backend.room.dto.request.PlayerRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

data class GameDataDto(
    var gameName: String,
    // 게임 유형 (BATTLE, COOPERATION)
    var gameType: String,
    // 배틀 모드: 레드 팀 참가자
    var redTeam: List<PlayerRequest>? = null,
    // 배틀 모드: 블루 팀 참가자
    var blueTeam: List<PlayerRequest>? = null,
    // 협동 모드: 참가자
    var players: List<PlayerRequest>? = null,
    // 배틀 모드: 레드 팀 퍼즐 진행률 (%)
    var redProgressPercent: Double? = null,
    // 배틀 모드: 블루 팀 퍼즐 진행률 (%)
    var blueProgressPercent: Double? = null,
    // 퍼즐 이미지 URL
    var puzzleImage: String,
    // 퍼즐 조각 수
    var totalPieceCount: Int,
    // 퍼즐 시작 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    var startTime: LocalDateTime,
    // 퍼즐 종료 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    var finishTime: LocalDateTime,
) {
    fun toGameRecordDto(
        userId: Long,
        myTeam: String?,
        gameStatus: String?,
        withTeam: List<String>,
        vsTeam: List<String>,
        myPercent: Double,
    ): GameRecordDto {
        val durationInMinutes = (this.finishTime.second - this.startTime.second)

        return GameRecordDto(
            userId = userId,
            gameName = this.gameName,
            gameType = this.gameType,
            players = players?.map { it.playerName },
            puzzleImage = this.puzzleImage,
            totalPieceCount = this.totalPieceCount,
            durationInMinutes = durationInMinutes,
            playedAt = this.finishTime.plusHours(9),
            teamMates = ObjectMapper().writeValueAsString(withTeam),
            opponents = ObjectMapper().writeValueAsString(vsTeam),
            myTeam = myTeam,
            myPercent = myPercent,
            gameStatus = gameStatus,
        )
    }

    // Date -> LocalDateTime 변환
    private fun Date.toLocalDateTime(): LocalDateTime {
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}
