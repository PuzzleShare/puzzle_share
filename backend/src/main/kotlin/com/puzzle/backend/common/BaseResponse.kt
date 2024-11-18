package com.puzzle.backend.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.puzzle.backend.common.status.ResultCode

data class BaseResponse<T>(
    // 결과 코드
    val resultCode: String = ResultCode.SUCCESS.name,
    // 조회시 데이터를 담아서 반환해줄 data
    val data: T? = null,
    // 처리 메세지
    val message: String = ResultCode.SUCCESS.msg,
) {
    fun toJson() = ObjectMapper().writeValueAsString(this)
}
