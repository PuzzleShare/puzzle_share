package com.puzzle.backend.friend.service

import com.puzzle.backend.friend.domain.Friend
import com.puzzle.backend.friend.domain.RequestStatus
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
        val friends = friendRepository.findByRequesterAndStatus(user, RequestStatus.ACCEPTED) +
            friendRepository.findByReceiverAndStatus(user, RequestStatus.ACCEPTED)
        return friends.map { if (it.requester == user) it.receiver else it.requester }
    }

    // 친구 요청 보내기
    fun sendFriendRequest(
        requesterId: Long,
        receiverId: Long,
    ): Boolean {
        val requester = userRepository.findById(requesterId).orElseThrow { IllegalArgumentException("요청자를 찾을 수 없습니다.") }
        val receiver = userRepository.findById(receiverId).orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

        // 이미 친구 관계인지 확인
        if (friendRepository.findByRequesterAndReceiver(requester, receiver) != null) {
            return false // 이미 친구 관계이거나 요청이 존재함
        }

        // 새로운 친구 요청 생성
        val friendRequest = Friend(requester = requester, receiver = receiver)
        friendRepository.save(friendRequest)
        return true
    }

    // 친구 요청 수락하기
    fun acceptFriendRequest(
        requesterId: Long,
        receiverId: Long,
    ): Boolean {
        val requester = userRepository.findById(requesterId).orElseThrow { IllegalArgumentException("요청자를 찾을 수 없습니다.") }
        val receiver = userRepository.findById(receiverId).orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

        // 친구 요청이 존재하는지 확인
        val friendRequest = friendRepository.findByRequesterAndReceiver(requester, receiver)
            ?: return false // 요청이 존재하지 않으면 false 반환

        if (friendRequest.status == RequestStatus.PENDING) {
            // 요청을 수락하고 상태 변경
            friendRequest.status = RequestStatus.ACCEPTED
            friendRepository.save(friendRequest)
            return true
        }

        return false // 이미 수락된 요청이거나 잘못된 상태
    }

    // 친구 요청 거절하기
    fun rejectFriendRequest(
        requesterId: Long,
        receiverId: Long,
    ): Boolean {
        val requester = userRepository.findById(requesterId).orElseThrow { IllegalArgumentException("요청자를 찾을 수 없습니다.") }
        val receiver = userRepository.findById(receiverId).orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

        // 친구 요청이 존재하는지 확인
        val friendRequest = friendRepository.findByRequesterAndReceiver(requester, receiver)
        friendRequest?.let {
            friendRepository.delete(it)
            return true
        }

        return false // 친구 요청이 존재하지 않음
    }

    // 친구 삭제 기능
    fun removeFriend(
        userId: Long,
        friendId: Long,
    ): Boolean {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val friend = userRepository.findById(friendId).orElseThrow { IllegalArgumentException("친구를 찾을 수 없습니다.") }

        val relationship = friendRepository.findByRequesterAndReceiver(user, friend)
            ?: friendRepository.findByRequesterAndReceiver(friend, user)
        relationship?.let {
            friendRepository.delete(it)
            return true
        }
        return false // 친구 관계가 아님
    }
}
