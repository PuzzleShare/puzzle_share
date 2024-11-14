package com.puzzle.websocket.puzzle.dto


// Kotlin에서 data class로 선언했기 때문에,
// equals()와 hashCode() 메서드가 id 필드에 따라 자동으로 생성됨.
// 따라서 코드가 더 간결해지고 유지보수가 쉬워짐.
data class User(
    val id: String
) {
    // equals() 메서드는 id가 같으면
    // 두 객체가 동일하다고 판단하도록 구현
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.id == other.id
    }

    // data class의 기본 hashCode()는
    // id 필드를 기반으로 생성되므로 명시적으로 구현하지 않아도 된다.
    override fun hashCode(): Int {
        return id.hashCode()
    }
}
