package com.puzzle.backend.common.oauth.domain

import com.puzzle.backend.common.domain.BaseEntity
import com.puzzle.backend.common.oauth.dto.response.UserDataResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(name = "users",
    indexes = [Index(name = "idx_users_email", columnList = "socialType, email")])
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
) : BaseEntity(){
    fun toUserDataResponse():UserDataResponse = UserDataResponse(userName, userImage, email, socialType)
}
