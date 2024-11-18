package com.puzzle.backend.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("dev", "local")
@Configuration
class ApiDocsConfig {
    @Bean
    fun publicApi(): GroupedOpenApi {
        // pathsToMatch로 원하는 경로의 api만 나오도록 설정
        return GroupedOpenApi
            .builder()
            .group("API")
            .pathsToMatch("/api/**")
            .build()
    }

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Puzzle Share API")
                    .description("퍼즐 쉐어 API 문서입니다.")
                    .version("1.0.0"),
            )
}
