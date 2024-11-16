package com.puzzle.backend.common.oauth.controller

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import com.puzzle.backend.common.oauth.service.UsersService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UsersController(
    private val usersService: UsersService
) {
    @GetMapping("/info")
    fun getUserInfo(request: HttpServletRequest): LoginSuccessResponse = usersService.getUserInfo(request)
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): BaseResponse<String> = usersService.logout(request)
}
