package com.puzzle.backend.record.controller

import com.puzzle.backend.oauth.repository.UsersRepository
import com.puzzle.backend.record.dto.GameDataDto
import com.puzzle.backend.record.dto.GameRecordDto
import com.puzzle.backend.record.dto.UserGameRecordsResponse
import com.puzzle.backend.record.service.GameRecordService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
    private val usersRepository: UsersRepository,
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
        // 페이지 번호 (기본값 0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int,
        // 페이지 크기 (기본값 10)
        @RequestParam(required = false, defaultValue = "10")
        size: Int,
    ): ResponseEntity<UserGameRecordsResponse> {
        val user = usersRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User not found with ID: $userId")
        }

        // Pageable 생성
        val pageable: Pageable = PageRequest.of(page, size)

        // 페이징된 게임 기록 가져오기
        val pagedGameRecords: Page<GameRecordDto> = gameRecordService.getPagedGameRecords(userId, gameType, pageable)

        val response = UserGameRecordsResponse(
            userId = user.userId,
            userName = user.userName,
            userImage = user.userImage,
            winRate = user.winRate,
            winCount = user.winCount,
            lossCount = user.lossCount,
            drawCount = user.drawCount,
            totalGames = user.totalGames,
            // 페이징된 결과의 내용만 추가
            records = pagedGameRecords.content,
            totalPages = pagedGameRecords.totalPages,
            totalElements = pagedGameRecords.totalElements,
            currentPage = pagedGameRecords.number,
        )
        return ResponseEntity.ok(response)
    }
}
