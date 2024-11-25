package com.puzzle.backend.record.service

import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.oauth.repository.UsersRepository
import com.puzzle.backend.record.dto.GameDataDto
import com.puzzle.backend.record.dto.GameRecordDto
import com.puzzle.backend.record.repository.GameRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameRecordService(
    private val usersRepository: UsersRepository,
    private val gameRecordRepository: GameRecordRepository
) {

    @Transactional
    fun handleGameEnd(gameDataDto: GameDataDto, userId: Long) {
        println("Handling game end for userId: $userId")
        val user = usersRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User not found with ID: $userId")
        }

        when (gameDataDto.gameType) {
            "BATTLE" -> handleBattleMode(gameDataDto, user)
            "COOPERATION" -> handleCooperationMode(gameDataDto, user)
            else -> throw IllegalArgumentException("Invalid game type: ${gameDataDto.gameType}")
        }
    }

    private fun handleBattleMode(gameDataDto: GameDataDto, user: Users) {
        val winnerTeam = determineWinner(
            gameDataDto.redProgressPercent ?: 0,
            gameDataDto.blueProgressPercent ?: 0
        )

        if (winnerTeam == "DRAW") {
            saveDrawRecords(gameDataDto, user)
        } else {
            saveBattleRecords(gameDataDto, user, winnerTeam)
        }
    }

    private fun determineWinner(redProgress: Int, blueProgress: Int): String {
        return when {
            redProgress > blueProgress -> "RED"
            redProgress < blueProgress -> "BLUE"
            else -> "DRAW"
        }
    }

    private fun saveBattleRecords(gameDataDto: GameDataDto, user: Users, winnerTeam: String) {
        val myTeam = determineUserTeam(gameDataDto, user)
        val isWinningTeam = winnerTeam == myTeam

        val (withTeam, vsTeam) = determineBattleTeams(gameDataDto, user, myTeam)

        val gameRecordDto = gameDataDto.toGameRecordDto(
            userId = user.userId,
            myTeam = myTeam,
            gameStatus = if (isWinningTeam) "WIN" else "LOSS",
            withTeam = withTeam,
            vsTeam = vsTeam
        )

        val gameRecord = gameRecordDto.toEntity(user)
        gameRecordRepository.save(gameRecord)

        updateUserStats(user, if (isWinningTeam) "WIN" else "LOSS")
    }

    private fun saveDrawRecords(gameDataDto: GameDataDto, user: Users) {
        val myTeam = determineUserTeam(gameDataDto, user)
        val (withTeam, vsTeam) = determineBattleTeams(gameDataDto, user, myTeam)

        val gameRecordDto = gameDataDto.toGameRecordDto(
            userId = user.userId,
            myTeam = myTeam,
            gameStatus = "DRAW",
            withTeam = withTeam,
            vsTeam = vsTeam
        )

        val gameRecord = gameRecordDto.toEntity(user)
        gameRecordRepository.save(gameRecord)

        updateUserStats(user, "DRAW")
    }

    private fun determineUserTeam(gameDataDto: GameDataDto, user: Users): String? {
        return when {
            gameDataDto.redTeam?.any { it.playerId == user.userId } == true -> "RED"
            gameDataDto.blueTeam?.any { it.playerId == user.userId } == true -> "BLUE"
            else -> null
        }
    }

    private fun determineBattleTeams(
        gameDataDto: GameDataDto,
        user: Users,
        myTeam: String?
    ): Pair<List<Long>, List<Long>> {
        return when (myTeam) {
            "RED" -> Pair(
                gameDataDto.redTeam!!.filter { it.playerId != user.userId }.map { it.playerId },
                gameDataDto.blueTeam?.map { it.playerId } ?: emptyList()
            )
            "BLUE" -> Pair(
                gameDataDto.blueTeam!!.filter { it.playerId != user.userId }.map { it.playerId },
                gameDataDto.redTeam?.map { it.playerId } ?: emptyList()
            )
            else -> Pair(emptyList(), emptyList())
        }
    }

    @Transactional(readOnly = true)
    fun getUserGameRecords(userId: Long, gameType: String): List<GameRecordDto> {
        val user = usersRepository.findById(userId).orElseThrow()
        val records = if (gameType == "ALL") {
            gameRecordRepository.findByUser(user)
        } else {
            gameRecordRepository.findByUserAndGameType(user, gameType)
        }
        return records.map { it.toDto() }
    }

    private fun updateUserStats(user: Users, gameStatus: String) {
        when (gameStatus) {
            "WIN" -> user.updateOnWin()
            "LOSS" -> user.updateOnLoss()
            "DRAW" -> user.updateOnDraw()
            else -> throw IllegalArgumentException("Invalid game status: $gameStatus")
        }
        usersRepository.save(user)
    }

    private fun handleCooperationMode(gameDataDto: GameDataDto, user: Users) {
        val isPuzzleCompleted = gameDataDto.redProgressPercent == 100 // Assuming progress for cooperative mode
        val gameStatus = if (isPuzzleCompleted) "COMPLETED" else "FAILED"
        // 자기 자신을 제외한 팀원 리스트
        val withTeam = gameDataDto.players
            ?.filter { it.playerId != user.userId }
            ?.map { it.playerId } ?: emptyList()

        val gameRecordDto = gameDataDto.toGameRecordDto(
            userId = user.userId,
            myTeam = "COOPERATION",
            gameStatus = gameStatus,
            withTeam = withTeam,
            vsTeam = emptyList()
        )

        val gameRecord = gameRecordDto.toEntity(user)
        gameRecordRepository.save(gameRecord)

        updateUserStats(user, if (isPuzzleCompleted) "WIN" else "LOSS")
    }

}
