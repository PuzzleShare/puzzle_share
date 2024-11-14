package com.puzzle.backend.common.oauth.handler

import com.puzzle.backend.common.oauth.enums.SocialType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomLogoutSuccessHandler(
    @Value("\${oauth.credentials.google.logout-url}")
    private val googleLogourUrl: String,
    @Value("\${oauth.credentials.kakao.logout-url}")
    private val kakaoLogourUrl: String,
    @Value("\${oauth.credentials.naver.logout-url}")
    private val naverLogourUrl: String,
) : LogoutSuccessHandler {

    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        // 요청에서 OAuth 제공자 정보를 가져옵니다 (예: "google", "kakao", "naver")
        val url = when (request.getParameter("provider")) {
            SocialType.GOOGLE.name -> googleLogourUrl
            SocialType.KAKAO.name -> kakaoLogourUrl
            SocialType.NAVER.name -> naverLogourUrl
            else -> throw OAuth2AuthenticationException("Unsupported provider")
        }

        response.sendRedirect(url)
    }
}
