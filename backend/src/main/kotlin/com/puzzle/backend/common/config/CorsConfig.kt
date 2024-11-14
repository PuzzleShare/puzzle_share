package com.puzzle.backend.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val LOCAL_FRONT = "http://localhost:3000"
const val LOCAL_BACK = "http://localhost:8081"

@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // todo : 프론트 엔드 url 추가
            .allowedOrigins(LOCAL_FRONT, LOCAL_BACK)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
    }
}
