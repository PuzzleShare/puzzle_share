package com.puzzle.backend.common.oauth.handler

import com.puzzle.backend.common.oauth.enums.SocialType
import com.puzzle.backend.common.oauth.repository.UsersRepository
import com.puzzle.backend.common.oauth.service.JwtProvider
import com.puzzle.backend.room.service.RedisService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
    private val usersRepository: UsersRepository,
    @Value("\${login.redirect-url}")
    private val redirectUrl: String,
    private val redisService: RedisService
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val type = request.requestURI.substring(request.requestURI.lastIndexOf("/") + 1).uppercase()
        val dto = SocialType.valueOf(type).convert(oAuth2User.attributes)
        val user = usersRepository.findBySocialTypeAndEmail(dto.provider, dto.email)!!

        val accessToken = jwtProvider.createToken(user, 3_600_000)
        val refreshToken = jwtProvider.createToken(user, 24 * 60 * 60 * 1000)
        redisService.save("user${user.userId}", refreshToken)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.addHeader("Authorization", "Bearer $accessToken")

        response.sendRedirect("$redirectUrl?token=$accessToken")
    }
}
