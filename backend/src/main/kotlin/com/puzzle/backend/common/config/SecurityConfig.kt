package com.puzzle.backend.common.config

import com.puzzle.backend.common.oauth.filter.JwtAuthFilter
import com.puzzle.backend.common.oauth.handler.CustomLogoutSuccessHandler
import com.puzzle.backend.common.oauth.handler.OAuth2AuthenticationFailureHandler
import com.puzzle.backend.common.oauth.handler.OAuth2AuthenticationSuccessHandler
import com.puzzle.backend.common.oauth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val corsConfig: CorsConfig,
    private val customLogoutSuccessHandler: CustomLogoutSuccessHandler,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // 기본 인증 방식 비활성화 (JWT 사용을 위해)
            .csrf { it.disable() } // CSRF 보호 비활성화 (토큰 인증으로 대체)
            .cors { corsConfig }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션을 사용하지 않도록 설정 (JWT로 인증하기 때문)
            .authorizeHttpRequests {
                // 요청 URL별 권한 설정
                it.requestMatchers(
                    "/login/oauth2/code/**",
                    "/oauth2/authorization/**",
                    "/api/v1/test/**"
                ).permitAll()
                    .anyRequest().authenticated() // 나머지 모든 요청은 인증 확인
            }
            .oauth2Login {
                it.userInfoEndpoint { it.userService(customOAuth2UserService::loadUser) }
                it.successHandler(oAuth2AuthenticationSuccessHandler)
                it.failureHandler(oAuth2AuthenticationFailureHandler)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build() // 설정 완료 후 SecurityFilterChain 반환
    }
}
