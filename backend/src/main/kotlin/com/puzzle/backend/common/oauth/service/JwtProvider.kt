package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.oauth.domain.UserCache
import com.puzzle.backend.common.oauth.domain.Users
import com.puzzle.backend.common.oauth.handler.DAY
import com.puzzle.backend.common.oauth.handler.HOUR
import com.puzzle.backend.common.oauth.repository.UserCacheRepository
import com.puzzle.backend.common.oauth.repository.UsersRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@Service
class JwtProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
    private val userCacheRepository: UserCacheRepository,
    private val usersRepository: UsersRepository,
) {
    private val signKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createToken(
        user: Users,
        time: Long,
    ): String {
        val claims = Jwts.claims().setSubject(user.userId.toString())
        val now = Date()
        val expiryDate = Date(now.time + time) // 1시간 만료
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(signKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUid(token: String?): String {
        return Jwts.parserBuilder()
            .setSigningKey(signKey)
            .build()
            .parseClaimsJws(token).body.subject
    }

    fun refesh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean {
        return try {
            val refreshCookie = request.cookies?.find { it.name == "refresh" }!!
            val userId = getUid(refreshCookie.value)
            val cache = userCacheRepository.findById(userId.toLong()).orElseThrow()
            val user = usersRepository.findById(userId.toLong()).orElseThrow()
            val newRefreshToken = createToken(user, DAY * 1000)
            userCacheRepository.save(UserCache(userId = cache.userId, refreshToken = newRefreshToken))
            setCookie(createToken(user, HOUR * 1000), newRefreshToken, response)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setCookie(
        accessToken: String,
        refreshToken: String,
        response: HttpServletResponse,
    ) {
        val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val accessExpire = DateTimeFormatter.RFC_1123_DATE_TIME.format(now.plusHours(1))
        response.addHeader(
            "Set-Cookie",
            "jwt=$accessToken; Path=/; SameSite=None; Expires=$accessExpire",
        )

        val refreshExpire = DateTimeFormatter.RFC_1123_DATE_TIME.format(now.plusDays(1))
        response.addHeader(
            "Set-Cookie",
            "refresh=$refreshToken; Path=/; HttpOnly; Secure; SameSite=None; Expires=$refreshExpire",
        )
    }
}
