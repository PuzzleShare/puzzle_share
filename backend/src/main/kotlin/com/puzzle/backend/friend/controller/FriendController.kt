package com.puzzle.backend.friend.controller

import com.puzzle.backend.friend.dto.response.CombinedFriendResponse
import com.puzzle.backend.friend.dto.response.FriendDataResponse
import com.puzzle.backend.friend.service.FriendService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friend")
class FriendController(
    private val friendService: FriendService,
) {
    // 친구 목록 및 친구 요청 대기 조회 API
    @GetMapping("/{userId}")
    fun getFriends(
        @PathVariable userId: Long,
    ): CombinedFriendResponse = friendService.getFriends(userId)

    @GetMapping("/search")
    fun searchFriends(
        @RequestParam keyword: String,
    ): List<FriendDataResponse> = friendService.searchFriends(keyword)

    @DeleteMapping("/remove")
    fun removeFriend(
        @RequestParam userId: Long,
        @RequestParam friendId: Long,
    ): String =
        if (friendService.removeFriend(userId, friendId)) {
            "친구 삭제 성공"
        } else {
            "친구가 아닙니다."
        }

    // 친구 요청 보내기
    @PostMapping("/send")
    fun sendFriendRequest(
        @RequestParam requesterId: Long,
        @RequestParam receiverId: Long,
    ): String =
        if (friendService.sendFriendRequest(requesterId, receiverId)) {
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
        if (friendService.acceptFriendRequest(requesterId, receiverId)) {
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
        if (friendService.rejectFriendRequest(requesterId, receiverId)) {
            "친구 요청 거절 성공"
        } else {
            "친구 요청이 존재하지 않습니다."
        }
}
