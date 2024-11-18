package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import com.puzzle.backend.common.oauth.dto.response.RefreshDataResponse
import jakarta.servlet.http.HttpServletRequest

interface UsersService {
    fun getUserInfo(request: HttpServletRequest): LoginSuccessResponse

    fun logout(request: HttpServletRequest): BaseResponse<String>

    fun getRefreshData(request: HttpServletRequest): RefreshDataResponse
}
