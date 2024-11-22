package com.puzzle.backend.friend.service

import com.puzzle.backend.friend.domain.Friend
import com.puzzle.backend.friend.domain.FriendRequest
import com.puzzle.backend.friend.domain.RequestStatus
import com.puzzle.backend.friend.repository.FriendRepository
import com.puzzle.backend.friend.repository.FriendRequestRepository
import com.puzzle.backend.oauth.repository.UsersRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class FriendRequestService(
    private val friendRequestRepository: FriendRequestRepository,
    private val friendRepository: FriendRepository,
    private val userRepository: UsersRepository,
) {
    // 친구 요청 보내기
    fun sendFriendRequest(
        requesterId: Long,
        receiverId: Long,
    ): Boolean {
        val requester = userRepository.findById(requesterId).orElseThrow { IllegalArgumentException("요청자를 찾을 수 없습니다.") }
        val receiver = userRepository.findById(receiverId).orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

        // 이미 친구 관계인지 확인
        if (friendRepository.findByUserAndFriend(requester, receiver) != null) {
            return false // 이미 친구 관계임
        }

        // 이미 친구 요청이 있는지 확인
        if (friendRequestRepository.findByRequesterAndReceiver(requester, receiver) != null) {
            return false // 이미 요청이 존재함
        }

        // 새로운 친구 요청 생성
        val friendRequest = FriendRequest(requester = requester, receiver = receiver)
        friendRequestRepository.save(friendRequest)
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
        val friendRequest = friendRequestRepository.findByRequesterAndReceiver(requester, receiver)
            ?: return false // 요청이 존재하지 않으면 false 반환

        if (friendRequest.status == RequestStatus.PENDING) {
            // 요청을 수락하고 친구 관계 추가
            friendRepository.save(Friend(user = requester, friend = receiver))
            friendRequestRepository.delete(friendRequest) // 요청 삭제
            return true
        }

        return false // 이미 수락된 요청이거나 잘못된 상태
    }

    // 친구 요청 거절 기능
    fun rejectFriendRequest(
        requesterId: Long,
        receiverId: Long,
    ): Boolean {
        val requester = userRepository.findById(requesterId).orElseThrow { IllegalArgumentException("요청자를 찾을 수 없습니다.") }
        val receiver = userRepository.findById(receiverId).orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

        // 친구 요청이 존재하는지 확인
        val friendRequest = friendRequestRepository.findByRequesterAndReceiver(requester, receiver)
        friendRequest?.let {
            friendRequestRepository.delete(it)
            return true
        }

        return false // 친구 요청이 존재하지 않음
    }
}
