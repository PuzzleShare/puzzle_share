package com.puzzle.backend.friend.repository

import com.puzzle.backend.friend.domain.FriendRequest
import com.puzzle.backend.friend.domain.RequestStatus
import com.puzzle.backend.oauth.domain.Users
import org.springframework.data.jpa.repository.JpaRepository

interface FriendRequestRepository : JpaRepository<FriendRequest, Long> {
    // 특정 사용자가 받은 모든 친구 요청 조회
    fun findByReceiverAndStatus(
        receiver: Users,
        status: RequestStatus,
    ): List<FriendRequest>

    // 특정 사용자가 보낸 모든 친구 요청 조회
    fun findByRequesterAndStatus(
        requester: Users,
        status: RequestStatus,
    ): List<FriendRequest>

    // 특정 사용자 간의 친구 요청 조회
    fun findByRequesterAndReceiver(
        requester: Users,
        receiver: Users,
    ): FriendRequest?
}
