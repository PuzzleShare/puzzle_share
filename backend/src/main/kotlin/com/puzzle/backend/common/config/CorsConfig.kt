package com.puzzle.backend.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val LOCAL_FRONT = "http://localhost:3000"
const val LOCAL_BACK = "http://localhost:8080"
const val FRONT = "https://puzzle-frontend-five.vercel.app"

@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(LOCAL_FRONT, LOCAL_BACK, FRONT)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
