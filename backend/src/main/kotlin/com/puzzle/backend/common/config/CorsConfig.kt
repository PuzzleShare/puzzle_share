package com.puzzle.backend.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val LOCAL_FRONT = "http://localhost:5173"
const val LOCAL_BACK = "http://localhost:8080"

@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(LOCAL_FRONT, LOCAL_BACK)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
    }
}
