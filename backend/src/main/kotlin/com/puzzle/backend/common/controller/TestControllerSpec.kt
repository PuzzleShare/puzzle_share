package com.puzzle.backend.common.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "0. TEST용 API")
interface TestControllerSpec {
    @Operation(
        description = "성공 테스트 API",
        parameters = [
            Parameter(
                name = "name",
                description = "인사받을 이름",
                required = true,
                example = "kotlin",
            ),
        ],
    )
    fun success(name: String): String
}
