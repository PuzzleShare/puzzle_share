package com.puzzle.backend.record.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import com.puzzle.backend.room.dto.request.PlayerRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

data class GameDataDto(
    var gameType: String,             // 게임 유형 (BATTLE, COOPERATION)
    var redTeam: List<PlayerRequest>? = null, // 배틀 모드: 레드 팀 참가자
    var blueTeam: List<PlayerRequest>? = null, // 배틀 모드: 블루 팀 참가자
    var players: List<PlayerRequest>? = null, // 협동 모드: 참가자
    var redProgressPercent: Int? = null,      // 배틀 모드: 레드 팀 퍼즐 진행률 (%)
    var blueProgressPercent: Int? = null,     // 배틀 모드: 블루 팀 퍼즐 진행률 (%)
    var puzzleImage: String,          // 퍼즐 이미지 URL
    var totalPieceCount: Int,         // 퍼즐 조각 수
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    var startTime: LocalDateTime,              // 퍼즐 시작 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    var finishTime: LocalDateTime              // 퍼즐 종료 시간
) {
    fun toGameRecordDto(
        userId: Long,
        myTeam:String?,
        gameStatus: String?,
        withTeam:List<Long>,
        vsTeam:List<Long>
        ): GameRecordDto {
        val durationInMinutes = ((this.finishTime.minute-this.startTime.minute) / 1000 / 60).toInt()



        return GameRecordDto(
            userId = userId,
            gameType = this.gameType,
            players = players?.map { it.playerId },
            puzzleImage = this.puzzleImage,
            totalPieceCount = this.totalPieceCount,
            durationInMinutes = durationInMinutes,
            playedAt = this.finishTime,
            teamMates = ObjectMapper().writeValueAsString(withTeam),
            opponents = ObjectMapper().writeValueAsString(vsTeam),
            myTeam = myTeam,
            gameStatus = gameStatus
            )
    }

    // Date -> LocalDateTime 변환
    private fun Date.toLocalDateTime(): LocalDateTime {
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}
