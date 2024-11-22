package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import com.puzzle.backend.common.oauth.dto.response.RefreshDataResponse
import com.puzzle.backend.common.oauth.handler.HOUR
import com.puzzle.backend.common.oauth.repository.UserCacheRepository
import com.puzzle.backend.common.oauth.repository.UsersRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val jwtProvider: JwtProvider,
    private val userCacheRepository: UserCacheRepository,
    @Value("\${spring.profiles.active}")
    private val active: String,
) : UsersService {
    private val frontDomain = if (active == "local") {
        "localhost"
    } else {
        "puzzle-frontend-five.vercel.app"
    }

    override fun getUserInfo(request: HttpServletRequest): LoginSuccessResponse {
        val cookie = request.cookies?.find { it.name == "refresh" }
        val userId = jwtProvider.getUid(cookie?.value)
        val user = usersRepository.findById(userId.toLong()).orElseThrow()
        return LoginSuccessResponse.of(user)
    }

    override fun logout(request: HttpServletRequest): BaseResponse<String> {
        val token = request.getHeader("Authorization").removePrefix("Bearer ")
        val userId = jwtProvider.getUid(token)
        val userCache = userCacheRepository.findById(userId.toLong())
        userCache.ifPresent(userCacheRepository::delete)

        return BaseResponse(
            resultCode = HttpStatus.OK.name,
            data = "로그아웃 성공",
            message = "로그아웃 성공",
        )
    }

    override fun getRefreshData(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): RefreshDataResponse {
        val refreshToken = request.cookies?.find { it.name == "refresh" }!!
        val userId = jwtProvider.getUid(refreshToken.value)
        val user = usersRepository.findById(userId.toLong()).orElseThrow()

        val accessToken = jwtProvider.createToken(user, HOUR * 1000)
        val hour = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1)
        val hourFormatted = hour.format(DateTimeFormatter.RFC_1123_DATE_TIME)
        response.addHeader(
            "Set-Cookie",
            "jwt=$accessToken; Domain=$frontDomain; Path=/; Secure; SameSite=None; Expires=$hourFormatted",
        )

        return RefreshDataResponse(
            userId = user.userId,
            userName = user.userName,
            email = user.email,
            image = user.userImage,
            provider = user.socialType,
            token = accessToken,
        )
    }
}
