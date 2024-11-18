package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.dto.response.LoginSuccessResponse
import com.puzzle.backend.common.oauth.dto.response.RefreshDataResponse
import com.puzzle.backend.common.oauth.handler.HOUR
import com.puzzle.backend.common.oauth.repository.UserCacheRepository
import com.puzzle.backend.common.oauth.repository.UsersRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val jwtProvider: JwtProvider,
    private val userCacheRepository: UserCacheRepository,
) : UsersService {
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

    override fun getRefreshData(request: HttpServletRequest): RefreshDataResponse {
        val refreshToken = request.cookies?.find { it.name == "refresh" }!!
        val userId = jwtProvider.getUid(refreshToken.value)
        val user = usersRepository.findById(userId.toLong()).orElseThrow()

        return RefreshDataResponse(
            userId = user.userId,
            userName = user.userName,
            email = user.email,
            image = user.userImage,
            provider = user.socialType,
            token = jwtProvider.createToken(user, HOUR * 1000)
        )

    }
}
