import com.puzzle.backend.oauth.domain.Users

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "game_records")
data class GameRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 (IDENTITY 전략)
    val recordId: Long = 0, // 전적 고유 ID

    @ManyToOne(fetch = FetchType.LAZY) // 사용자와의 관계 매핑
    @JoinColumn(name = "user_id", nullable = false)
    val user: Users, // 게임 전적과 연결된 사용자

    val gameId: String, // 게임의 고유 ID

    @Enumerated(EnumType.STRING)
    val gameType: String, // 게임 모드 (BATTLE, COOPERATIVE 등)

    val participants: String, // JSON 형태의 참여자 정보 (팀 구성 등)

    val winnerTeam: String? = null, // 배틀 모드: 승리 팀 (RED, BLUE, DRAW), 협동 모드: null

    val puzzleImage: String, // 사용된 퍼즐 이미지 URL

    val totalPieceCount: Int, // 퍼즐 조각 수

    val durationInMinutes: Int, // 퍼즐 완료 시간 (분)

)
