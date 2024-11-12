package com.puzzle.websocket.puzzle.service

class PuzzleRoomServiceImpl : PuzzleRoomService {
    override fun enterRoom() {
        TODO("방에 자리가 있는지 확인")
        TODO("방에 자리가 있으면 사람이 부족한 쪽으로 팀을 설정하고 입장")
        TODO("방 안에 있는 사람들한테 메시지 전송")
    }

    override fun leaveRoom() {
        TODO("방에서 멤버를 뺌")
        TODO("방에 있는 사람들에게 빠졌다는 메시지 전송")
    }

    override fun moveTeam() {
        TODO("반대쪽 팀에 자리가 있는지 확인")
        TODO("자리가 있으면 팀 변경")
        TODO("자리가 변경되면 방 안의 사람들에게 변경된 자리 전송")
    }

    override fun gameStart() {
        TODO("이미지 및 게임 상태를 저장")
        TODO("방 안의 사람들에게 게임시작 메시지 전송")
    }
}
