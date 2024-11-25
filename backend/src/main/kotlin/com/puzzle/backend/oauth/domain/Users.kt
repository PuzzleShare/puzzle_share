package com.puzzle.backend.oauth.domain

import com.puzzle.backend.common.domain.BaseEntity
import com.puzzle.backend.oauth.dto.response.UserDataResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "users",
    indexes = [Index(name = "idx_users_email", columnList = "socialType, email")],
)
class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long = 0,
    var userName: String,
    @Column(updatable = false)
    var email: String,
    @Column(updatable = false)
    var socialType: String,
    var userImage: String,
    @Column(columnDefinition = "INT DEFAULT 0")
    var winCount: Int = 0,
    @Column(columnDefinition = "INT DEFAULT 0")
    var lossCount: Int = 0,
    @Column(columnDefinition = "INT DEFAULT 0")
    var drawCount: Int = 0,
    @Column(columnDefinition = "INT DEFAULT 0")
    var totalGames: Int = 0,
    @Column(columnDefinition = "INT DEFAULT 0")
    var winRate: Double = 0.0,
) : BaseEntity() {
    fun toUserDataResponse(): UserDataResponse = UserDataResponse(userName, userImage, email, socialType)

    fun updateOnWin() {
        winCount += 1
        totalGames += 1
        calculateWinRate()
    }

    fun updateOnLoss() {
        lossCount += 1
        totalGames += 1
        calculateWinRate()
    }

    fun updateOnDraw() {
        drawCount++
    }

    private fun calculateWinRate() {
        winRate = if (totalGames > 0) {
            (winCount.toDouble() / totalGames) * 100
        } else {
            0.0
        }
    }
}
