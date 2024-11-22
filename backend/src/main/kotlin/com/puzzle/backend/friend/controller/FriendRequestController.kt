package com.puzzle.backend.friend.controller

import com.puzzle.backend.friend.service.FriendRequestService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friend/requests")
class FriendRequestController(
    private val friendRequestService: FriendRequestService,
) {
    // 친구 요청 보내기
    @PostMapping("/send")
    fun sendFriendRequest(
        @RequestParam requesterId: Long,
        @RequestParam receiverId: Long,
    ): String =
        if (friendRequestService.sendFriendRequest(requesterId, receiverId)) {
            "친구 요청을 성공적으로 보냈습니다."
        } else {
            "이미 친구이거나 이미 요청이 존재합니다."
        }

    // 친구 요청 수락하기
    @PostMapping("/accept")
    fun acceptFriendRequest(
        @RequestParam requesterId: Long,
        @RequestParam receiverId: Long,
    ): String =
        if (friendRequestService.acceptFriendRequest(requesterId, receiverId)) {
            "친구 요청을 수락했습니다."
        } else {
            "유효하지 않은 친구 요청입니다."
        }

    // 친구 요청 거절하기
    @DeleteMapping("/reject")
    fun rejectFriendRequest(
        @RequestParam requesterId: Long,
        @RequestParam receiverId: Long,
    ): String =
        if (friendRequestService.rejectFriendRequest(requesterId, receiverId)) {
            "친구 요청 거절 성공"
        } else {
            "친구 요청이 존재하지 않습니다."
        }
}
