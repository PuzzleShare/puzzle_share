package com.puzzle.backend.friend.service

import com.puzzle.backend.friend.repository.FriendRepository
import com.puzzle.backend.oauth.domain.Users
import com.puzzle.backend.oauth.repository.UsersRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UsersRepository,
) {
    // 친구 목록 조회 기능
    fun getFriends(userId: Long): List<Users> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val friends = friendRepository.findByUser(user)
        return friends.map { it.friend }
    }

    // 친구 삭제 기능
    fun removeFriend(
        userId: Long,
        friendId: Long,
    ): Boolean {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val friend = userRepository.findById(friendId).orElseThrow { IllegalArgumentException("친구를 찾을 수 없습니다.") }

        val relationship = friendRepository.findByUserAndFriend(user, friend)
        relationship?.let {
            friendRepository.delete(it)
            return true
        }
        return false // 친구 관계가 아님
    }
}
