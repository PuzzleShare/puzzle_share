package com.puzzle.backend.common.oauth.controller

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import com.puzzle.backend.common.oauth.dto.response.RefreshDataResponse
import com.puzzle.backend.common.oauth.service.UsersService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UsersController(
    private val usersService: UsersService,
) : UsersControllerSpec {
    @GetMapping("/info")
    override fun getUserInfo(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): LoginSuccessResponse = usersService.getUserInfo(request, response)

    @GetMapping("/logout")
    override fun logout(request: HttpServletRequest): BaseResponse<String> = usersService.logout(request)

    @GetMapping("/refresh")
    fun refeshToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): RefreshDataResponse = usersService.getRefreshData(request, response)
}
