package com.puzzle.backend.common.exception.custom

class ImageValidationException(
    message: String = "이미지가 유효하지 않습니다.",
) : RuntimeException(message)
