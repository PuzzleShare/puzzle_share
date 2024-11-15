package com.puzzle.backend.common.oauth.filter

import com.puzzle.backend.common.oauth.service.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class JwtAuthFilter(
    private val jwtProvider: JwtProvider
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        val header = request.getHeader("Authorization")
        val token = header.removePrefix("Bearer ")
        if (jwtProvider.validateToken(token)) {
            val userId = jwtProvider.getUid(token)
            val auth = UsernamePasswordAuthenticationToken(userId, "", emptyList())
            auth.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }
}
