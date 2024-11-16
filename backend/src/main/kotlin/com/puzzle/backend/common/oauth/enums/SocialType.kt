package com.puzzle.backend.common.oauth.enums

import com.puzzle.backend.common.oauth.dto.response.UserDataResponse
import java.util.function.Function

enum class SocialType(
    private val function: Function<Map<String, Any>, UserDataResponse>,
) {
    GOOGLE(
        Function {
            UserDataResponse(
                userName = it["name"] as String,
                image = it["picture"] as String,
                email = it["email"] as String,
                provider = "GOOGLE",
            )
        }
    ),
    KAKAO(
        Function {
            UserDataResponse(
                userName = (it["properties"] as Map<*, *>)["nickname"] as String,
                image = (it["properties"] as Map<*, *>)["thumbnail_image"] as String,
                email = (it["kakao_account"] as Map<*, *>)["email"] as String,
                provider = "KAKAO",
            )
        }
    ),
    NAVER(
        Function {
            UserDataResponse(
                userName = (it["response"] as Map<*, *>)["nickname"] as String,
                image = (it["response"] as Map<*, *>)["profile_image"] as String,
                email = (it["response"] as Map<*, *>)["email"] as String,
                provider = "NAVER",
            )
        }
    );

    fun convert(attributes: Map<String, Any>): UserDataResponse = function.apply(attributes)
}
