package com.puzzle.backend.common

import com.puzzle.backend.common.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name, // 결과 코드
    val data: T? = null, // 조회시 데이터를 담아서 반환해줄 data
    val message: String = ResultCode.SUCCESS.msg, // 처리 메세지
)
