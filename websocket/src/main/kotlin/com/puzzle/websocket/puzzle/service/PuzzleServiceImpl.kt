package com.puzzle.websocket.puzzle.service

import org.springframework.messaging.simp.SimpMessagingTemplate

class PuzzleServiceImpl(
    private val template: SimpMessagingTemplate,
) : PuzzleService {
    override fun matchPiece() {
        TODO("조각의 위치를 기준으로 다른 조각과 연결되는지 확인")
        TODO("연결되면 다른 조각과 연결")
        TODO("연결되었다고 메시지 전송")
        TODO("모든 조각이 연결되었으면 게임 종료 및 승리팀 전송")
        TODO("게임이 종료되었으면 방 삭제(나중에 수정될 수도 있음)")
    }

    override fun movePiece() {
        TODO("조각을 다른 사람이 움직이고 있는지 확인(다른 사람이 움직이고 있으면 그냥 return)")
        TODO("조각의 소유자 변경")
        TODO("새로운 위치로 조각의 위치 변경")
        TODO("변경된 위치 메시지로 전송")
    }
}
