

import java.time.LocalDateTime


data class GameInfoDto(
    var id: Long = 0L,
    var type: String = "BATTLE",
    var isCleared: Boolean? = false,

    var curPlayerCount: Int = 0,
    var maxPlayerCount: Int = 4,
    var totalPieceCount: Int = 50,

    var limitTime: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    var passedTime: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    var startedTime: LocalDateTime = LocalDateTime.now(),
    var finishedTime: LocalDateTime = LocalDateTime.now().plusMinutes(5),
)