package com.puzzle.backend.friend.repository

import com.puzzle.backend.friend.domain.Friend
import com.puzzle.backend.oauth.domain.Users
import org.springframework.data.jpa.repository.JpaRepository

interface FriendRepository : JpaRepository<Friend, Long> {
    // 특정 사용자의 친구 목록을 가져오기
    fun findByUser(user: Users): List<Friend>

    // 두 사용자의 친구 관계를 확인하기
    fun findByUserAndFriend(
        user: Users,
        friend: Users,
    ): Friend?
}
