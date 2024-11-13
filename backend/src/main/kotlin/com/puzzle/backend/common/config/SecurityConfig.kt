package com.puzzle.backend.common.config

import com.puzzle.backend.common.oauth.handler.OAuth2AuthenticationFailureHandler
import com.puzzle.backend.common.oauth.handler.OAuth2AuthenticationSuccessHandler
import com.puzzle.backend.common.oauth.service.CustomOAuth2UserService
import com.puzzle.backend.common.oauth.service.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val jwtProvider: JwtProvider,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val corsConfig: CorsConfig
){
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // 기본 인증 방식 비활성화 (JWT 사용을 위해)
            .csrf { it.disable() } // CSRF 보호 비활성화 (토큰 인증으로 대체)
            .cors { corsConfig }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션을 사용하지 않도록 설정 (JWT로 인증하기 때문)
            .authorizeHttpRequests {
                // 요청 URL별 권한 설정
                it.requestMatchers("/api/logout", "/login/oauth2/code/**", "/oauth2/authorization/**").permitAll()
                    .anyRequest().authenticated() // 나머지 모든 요청은 인증 확인
            }.oauth2Login {
                it.userInfoEndpoint { it.userService(customOAuth2UserService::loadUser) }
                it.successHandler(OAuth2AuthenticationSuccessHandler(jwtProvider))
                it.failureHandler(OAuth2AuthenticationFailureHandler())
            }

        return http.build() // 설정 완료 후 SecurityFilterChain 반환
    }
}
