package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.oauth.domain.Users
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
) {
    private val signKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createToken(user: Users, time: Long): String {
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
}
