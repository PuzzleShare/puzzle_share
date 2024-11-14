package com.puzzle.backend.common.oauth.handler

import com.puzzle.backend.common.BaseResponse
import com.puzzle.backend.common.oauth.enums.SocialType
import com.puzzle.backend.common.oauth.service.JwtProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val token = jwtProvider.createToken(oAuth2User.name)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.setHeader("Authorization", "Bearer $token")
        val type = request.requestURI.substring(request.requestURI.lastIndexOf("/") + 1).uppercase()

        val jsonResponse = BaseResponse(
            message = "로그인 성공",
            data = SocialType.valueOf(type).convert(oAuth2User.attributes),
            resultCode = HttpServletResponse.SC_OK.toString()
        ).toJson()
        response.writer.write(jsonResponse)
    }
}
