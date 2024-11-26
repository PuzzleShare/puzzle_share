package com.puzzle.backend.friend.repository

import com.puzzle.backend.friend.domain.Friend
import com.puzzle.backend.friend.domain.RequestStatus
import com.puzzle.backend.oauth.domain.Users
import org.springframework.data.jpa.repository.JpaRepository

interface FriendRepository : JpaRepository<Friend, Long> {
    // 특정 사용자가 보낸 모든 친구 요청 조회

//    fun findByRequesterAndStatus(
//        requester: Users,
//        status: RequestStatus,
//    ): List<Friend>
//
//    // 특정 사용자가 받은 모든 친구 요청 조회
//    fun findByReceiverAndStatus(
//        receiver: Users,
//        status: RequestStatus,
//    ): List<Friend>

    // 두 사용자 간의 관계 확인
    fun findByRequesterAndReceiver(
        requester: Users,
        receiver: Users,
    ): Friend?

    // 특정 사용자의 친구 목록 조회
    fun findByRequesterAndStatus(
        requester: Users,
        status: RequestStatus,
    ): List<Friend>

    // 특정 사용자가 친구로 등록된 목록 조회
    fun findByReceiverAndStatus(
        receiver: Users,
        status: RequestStatus,
    ): List<Friend>
}
