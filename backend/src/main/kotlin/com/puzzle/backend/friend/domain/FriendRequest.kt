package com.puzzle.backend.friend.domain

import com.puzzle.backend.common.domain.BaseEntity
import com.puzzle.backend.oauth.domain.Users
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "friend_requests")
class FriendRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    // 요청을 보낸 사용자
    @ManyToOne
    @JoinColumn(name = "requester_id")
    var requester: Users,
    // 요청을 받은 사용자
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    var receiver: Users,
    // 요청의 상태 (PENDING, ACCEPTED, REJECTED)
    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,
) : BaseEntity()

enum class RequestStatus {
    PENDING,
}
