package com.puzzle.backend.common.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    // 데이터 저장
    fun save(
        key: String,
        value: Any,
    ) {
        valueOperations.set(key, value)
    }

    // 데이터 저장 (유효 기간 설정)
    fun saveWithExpiration(
        key: String,
        value: Any,
        timeout: Long,
        timeUnit: TimeUnit,
    ) {
        valueOperations.set(key, value, timeout, timeUnit)
    }

    // 데이터 조회
    fun find(key: String): Any? = valueOperations.get(key)

    // 데이터 삭제
    fun delete(key: String): Boolean = redisTemplate.delete(key) ?: false

    // 키 존재 여부 확인
    fun exists(key: String): Boolean = redisTemplate.hasKey(key) ?: false

    // 데이터 만료 시간 설정
    fun setExpiration(
        key: String,
        timeout: Long,
        timeUnit: TimeUnit,
    ): Boolean = redisTemplate.expire(key, timeout, timeUnit) ?: false

    // 데이터의 남은 TTL(Time-To-Live) 확인
    fun getTTL(
        key: String,
        timeUnit: TimeUnit,
    ): Long? = redisTemplate.getExpire(key, timeUnit)

    // 모든 키 삭제
    fun clearAll() {
        redisTemplate.keys("*")?.let { keys ->
            redisTemplate.delete(keys)
        }
    }

    // 키 목록 가져오기 (예: 특정 패턴을 가진 키 검색)
    fun findKeysByPattern(pattern: String): Set<String>? = redisTemplate.keys(pattern)

    // 기존 키의 데이터 업데이트 (존재할 때만 업데이트)
    fun updateIfPresent(
        key: String,
        value: Any,
    ) {
        if (exists(key)) {
            save(key, value)
        }
    }

    // 특정 키의 값 증가 (숫자형 데이터에 적합)
    fun incrementValue(
        key: String,
        delta: Long,
    ): Long? = valueOperations.increment(key, delta)

    // 특정 키의 값 감소 (숫자형 데이터에 적합)
    fun decrementValue(
        key: String,
        delta: Long,
    ): Long? = valueOperations.decrement(key, delta)
}
