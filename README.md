<div align= "center">
    <img src="https://capsule-render.vercel.app/api?type=rounded&color=0:5D3CF5,100:C1FBCF&height=180&text=Puzzle%20Share&animation=&fontColor=ffffff&fontSize=70&desc=함께%20맞추고,%20함께%20겨룬다!%20온라인%20배틀퍼즐%20게임!&descAlignY=75" />
</div>

> 서비스 사이트 : [Puzzle Share](https://www.puzzleshare.site/)

## Contents


<br/>

## 팀원/역할
<img src="https://github.com/user-attachments/assets/c2d59d30-2fef-4d19-a222-5d5d79de9be3" alt="크래프톤 팀원 사진" width="700">

| 문지언 [GitHub](https://github.com/jifrozen0110) | 고병찬 [GitHub](https://github.com/LDK1009) | 김진성 [GitHub](https://github.com/LDK1009) | 정인우 [GitHub](https://github.com/LDK1009) | 최민재 [GitHub](https://github.com/LDK1009) |
|:------:|:------:|:------:|:------:|:------:|
| <img src="https://github.com/user-attachments/assets/11dc94cb-6d73-47a8-9900-40292cd0d149" alt="문지언" width="150"> | <img src="https://github.com/user-attachments/assets/a7380cbb-c7e1-4be3-a977-11e9390e630d" alt="고병찬" width="150"> | <img src="https://github.com/user-attachments/assets/4b9d7b09-beb0-48d5-8ac3-44dec9ad1a65" alt="김진성" width="150"> | <img src="https://github.com/user-attachments/assets/357c88d1-7684-4e4c-93c6-ff29644d89d1" alt="정인우" width="150"> | <img src="https://github.com/user-attachments/assets/33ac0898-46b0-471c-be12-8650b3dfba93" alt="최민재" width="150"> |
| BE/FE | BE/FE | BE/FE | BE/FE | BE/FE |
| - 인 게임 내 소켓 구성<br/>- 퍼즐 로직 구현<br/>- 이미지 퍼즐화 로직<br/>- 개발 dev, test 환경 분리 (submodule 구성)<br/>-aws 인프라 구성<br/> - 프론트 백 CI/CD 구성<br/>- 로드밸런서 서버 구현<br/>  | - 역할 추가 | - 퍼즐 로직 구현<br/>- 게임 대기방 api 및 소켓 구성<br/>- 친구 추가 및 사용자 검색 기능 구현<br/>- 게임 초대 기능 구현<br/> - 상대방 화면 구현<br/>- 로드 밸런서 서버 구현<br/> | - 게임 대기방 소켓 구성<br/>- 전체적인 화면 디자인 및 구현<br/>- 퍼즐 그룹화 및 진행률 로직 구현<br/>- 맞춰진 퍼즐 사이 균열 생기는 문제 해결<br/>- 퍼즐 데이터 패킷 최적화<br/>- 이미지 업로드(Amazon S3) | - OAuth2 <br/>- 맞춰진 퍼즐 사이 비틀어진 공간 생기는 문제 해결<br/>- 퍼즐 데이터 패킷 최적화<br/>- 아이템 구현<br/>- 캔버스 크기 변경에 따른 마우스이벤트 왜곡 문제 해결 |

<br/>

## 아키텍쳐
<img src="https://github.com/user-attachments/assets/cc7db166-fe7c-443f-ae0b-a11854e7d3a9" alt="아키텍쳐" width="700">

<br/>

## 주요 기능
### 화면
|||
|:------:|:------:|
| 홈(게임 리스트) | 마이페이지 |
| <img src="https://github.com/user-attachments/assets/894d0b21-334b-4255-a9ad-d45bdd4d6399" alt="홈(게임 리스트)" width="380"> | <img src="https://github.com/user-attachments/assets/f45aa01f-6912-44b1-bdf3-59ecddfb3bcc" alt="마이페이지" width="380"> |
| 갤러리 | 상세보기 |
| <img src="https://github.com/user-attachments/assets/72653656-7f78-440b-994e-be2fc7fcf4be" alt="갤러리" width="380"> | <img src="https://github.com/user-attachments/assets/b56325df-7695-4d21-b1fb-66d906dac4dc" alt="상세보기" width="380"> |
| 방만들기 | 게임 대기방 |
| <img src="https://github.com/user-attachments/assets/f6e1ee65-e304-4102-9f41-e827c8c19140" alt="방만들기" width="380"> | <img src="https://github.com/user-attachments/assets/a8343ac3-8193-4841-ad9d-e57429ac86fc" alt="게임 대기방" width="380"> |
| 게임 시작 | 완성한 퍼즐은 갤러리에 저장 |
| <img src="https://github.com/user-attachments/assets/d1b5d850-26f5-43fe-a368-1f8db149c857" alt="게임 시작" width="380"> | <img src="https://github.com/user-attachments/assets/48ca7993-6803-46d9-8387-fb810ce3fd8b" alt="완성한 퍼즐은 갤러리에 저장" width="380"> |


### 아이템
|||
|:------:|:------:|
| 아이템 퍼즐 (공격 아이템 랜덤 획득) | 먹물 (상대팀 화면 가리기) |
| <img src="https://github.com/user-attachments/assets/0af97df8-95b7-4d4c-847f-8c53809251d1" alt="아이템 퍼즐" width="380"> | <img src="https://github.com/user-attachments/assets/c3f17e6c-0fc9-43c2-a64f-0bff7d01f083" alt="먹물" width="380"> |
| 폭탄 (상대팀 퍼즐 연결 끊기) | 블랙홀 (상대팀 퍼즐 모으기) |
| <img src="https://github.com/user-attachments/assets/4e9a7ac5-3396-4eff-b036-d2050be3c9a3" alt="폭탄" width="380"> | <img src="https://github.com/user-attachments/assets/607c1a69-a2b1-40d3-b174-fb6d0b662360" alt="블랙홀" width="380"> |
| 태풍 (상대팀 퍼즐 흩뿌리기) | 액자 (우리팀 퍼즐 테두리 맞추기, 지는 팀 지원 아이템) |
| <img src="https://github.com/user-attachments/assets/f8a2bd62-6b75-4fb6-8894-ccfa810dbceb" alt="태풍" width="380"> | <img src="https://github.com/user-attachments/assets/2675c483-f923-49e9-89c1-18021081760e" alt="액자" width="380"> |

<br/>

## 문제 해결 / 개선
1. 퍼즐 결합
- 문제 원인
<img src="https://github.com/user-attachments/assets/98de9665-54e4-4496-8c4a-2ac0d2a182a2" alt="어긋난 퍼즐" width="380">
<img src="https://github.com/user-attachments/assets/980ca3f6-3a53-4c57-94de-36b822714098" alt="원인" width="380">

퍼즐을 결합했을 때 사이사이에 균열이 보이는 것을 확인<br/>
원인 : 퍼즐 조각을 만들기 위한 기준점이 퍼즐의 중심이 될 거라 생각했지만 비대칭적인 퍼즐 모양으로 인해 중심이 틀어짐. 조각의 중심을 기준으로 계산하기 때문에 중심이 어긋난 만큼 균열 발생

<br/>

- 아이디어
<img src="https://github.com/user-attachments/assets/fcfc4106-369f-48ca-aaa9-ceb56e5e68e0" alt="네모 퍼즐" width="380">
<img src="https://github.com/user-attachments/assets/a9b0ee68-d87f-4ebf-98c7-110ec7dce576" alt="아이디어" width="380">

아이디어1 : 퍼즐 조각 중심이 어긋난 만큼 보정값 계산 -> 퍼즐 조각의 모양이 다양하여 계산이 복잡하고 아무리 잘 계산해도 약간씩의 균열이 눈에 띔<br/>
아이디어2 : 퍼즐을 네모 모양으로 만들었을 경우 균열 없이 잘 결합되는 것을 확인 -> **"퍼즐을 감쌀 수 있는 크기의 투명한 사각형을 추가하면 될까?"** <br/>

<br/>

- 해결
<img src="https://github.com/user-attachments/assets/b7511018-0ebe-4e8f-a4ee-17c89986b67e" alt="딱 맞는 퍼즐" width="380">

두 번째 아이디어를 적용한 결과 중심이 어긋나지 않고 잘 결합되는 것을 확인

<br/>

2. 마우스 이벤트 왜곡
3. 패킷 수 최적화

<br/>

## 🛠️ Tech Stacks
### Backend
<div style="margin: ; text-align: left;" "text-align: left;">
    <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
    <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
    <img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=for-the-badge&logo=Amazon AWS&logoColor=white">
    <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
    <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
</div>

### Frontend
<div style="margin: ; text-align: left;" "text-align: left;">
    <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white">
    <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">
    <img src="https://img.shields.io/badge/Javascript-F7DF1E?style=for-the-badge&logo=Javascript&logoColor=white">
    <img src="https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=Vercel&logoColor=white">
</div>
