package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import com.puzzle.backend.common.oauth.dto.response.RefreshDataResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface UsersService {
    fun getUserInfo(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): LoginSuccessResponse

    fun logout(request: HttpServletRequest): BaseResponse<String>

    fun getRefreshData(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): RefreshDataResponse
}
