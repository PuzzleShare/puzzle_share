package com.puzzle.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean // Configuration에 @Component가 있는데 왜 사용? -> 메서드의 반환값을 스프링 컨텍스트에 Bean으로 등록
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // 기본 인증 방식 비활성화 (JWT 사용을 위해)
            .csrf { it.disable() } // CSRF 보호 비활성화 (토큰 인증으로 대체)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션을 사용하지 않도록 설정 (JWT로 인증하기 때문)
            .authorizeHttpRequests {
                // 요청 URL별 권한 설정
                it.anyRequest().permitAll() // 나머지 모든 요청은 인증 없이 접근 허용
            }

        return http.build() // 설정 완료 후 SecurityFilterChain 반환
    }
}
