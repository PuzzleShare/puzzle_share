package com.puzzle.backend.record.controller

import com.puzzle.backend.record.dto.GameDataDto
import com.puzzle.backend.record.dto.GameRecordDto
import com.puzzle.backend.record.service.GameRecordService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
class GameRecordController(
    private val gameRecordService: GameRecordService,
) {
    @PostMapping("/end/{userId}")
    fun handleGameEnd(
        @RequestBody
        gameDataDto: GameDataDto,
        @PathVariable
        userId: Long,
    ): ResponseEntity<String> {
        gameRecordService.handleGameEnd(gameDataDto, userId)
        return ResponseEntity.ok("Game ended and records updated successfully")
    }

    // 특정 유저의 게임 전적 조회
    @GetMapping("/{userId}/records")
    fun getUserGameRecords(
        @PathVariable
        userId: Long,
        @RequestParam(required = false, defaultValue = "ALL")
        gameType: String,
    ): ResponseEntity<List<GameRecordDto>> {
        val gameRecords = gameRecordService.getUserGameRecords(userId, gameType)
        return ResponseEntity.ok(gameRecords)
    }
}
