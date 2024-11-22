package com.puzzle.backend.common.oauth.controller

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Tag(name = "1. User API", description = "사용자 관련 API")
interface UsersControllerSpec {
    @Operation(
        summary = "사용자 정보 조회",
        description = "현재 로그인한 사용자의 정보를 조회합니다.",
        parameters = [
            Parameter(
                name = "request",
                description = "HttpServletRequest 객체 (자동 주입)",
                required = true,
            ),
        ],
    )
    fun getUserInfo(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): LoginSuccessResponse

    @Operation(
        summary = "로그아웃",
        description = "현재 사용자를 로그아웃합니다.",
        parameters = [
            Parameter(
                name = "request",
                description = "HttpServletRequest 객체 (자동 주입)",
                required = true,
            ),
        ],
    )
    fun logout(request: HttpServletRequest): BaseResponse<String>
}
