package com.puzzle.backend.friend.controller

import com.puzzle.backend.friend.service.FriendService
import com.puzzle.backend.oauth.dto.response.UserDataResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendController(
    private val friendService: FriendService,
) {
    // 친구 목록 조회 API
    @GetMapping("/{userId}")
    fun getFriends(
        @PathVariable userId: Long,
    ): List<UserDataResponse> {
        val friends = friendService.getFriends(userId)
        return friends.map { it.toUserDataResponse() }
    }

    // 친구 삭제 API
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
}
