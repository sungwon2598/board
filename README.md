# 🏫 서일대학교 ICT지원실 민원관리 시스템

## 📌 주요 기능
### 1. 이원화된 회원가입 시스템
#### 👨‍💼 직원용
- 관리자 승인 기반 회원가입 프로세스
- 승인 즉시 팀 Slack 초대 메일 자동 발송
- 부서별 권한 관리

#### 👨‍🎓 민원인용
- 학교 이메일 기반 인증 시스템
- 간편한 가입 프로세스
- 실시간 이메일 인증
![대시보드 이미지](./images/membernew.png)

### 2. 보안 강화 로그인 시스템
- 5회 로그인 실패 시 1시간 계정 잠금
- Remember-Me 기능으로 편리한 로그인 상태 유지
- 세션 기반 보안 관리
![대시보드 이미지](./images/image.png)

### 3. 직원 전용 대시보드
- 날짜별 민원 조회 시스템
  - 10개 단위 페이징 처리
  - 예약/일반 민원 구분 표시
- 실시간 민원 현황 모니터링
![대시보드 이미지](./images/main.png)

### 4. 스마트 민원 관리
#### ✍️ 민원 접수
- 예약/일반 민원 선택 옵션
- 다중 이미지 첨부 기능
- 자동 Slack 알림 시스템
  - Slack 발송 실패시 이메일 백업 발송

#### 📋 민원 처리
- AI 기반 자동 답변 시스템
- 관리자 민원 상태 관리
  - 조치완료
  - 야간이전
  - 보류
- 실시간 처리현황 업데이트
![민원접수 이미지](./images/boardnew.png)

### 5. 마이페이지
- 회원정보 관리
- 본인 접수 민원 및 댓글 이력 조회
![민원접수 이미지](./images/mypage.png)

### 6. 검색 시스템
- 상태별 민원 필터링
![민원접수 이미지](./images/search.png)


## 💻 기술 스택
<!-- 사용된 기술 스택 아이콘들 추가 예정 -->
| Category | Technologies |
|----------|-------------|
| Backend | ![Java](https://img.shields.io/badge/Java_17-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring_Security_6.1-%236DB33F.svg?style=for-the-badge&logo=spring-security&logoColor=white)  |
| Database | ![MySQL](https://img.shields.io/badge/MySQL-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) ![JPA](https://img.shields.io/badge/JPA-%23FF6F00.svg?style=for-the-badge&logo=hibernate&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-%23FF0000.svg?style=for-the-badge&logo=mybatis&logoColor=white)|
| Cloud | ![AWS EC2](https://img.shields.io/badge/AWS_EC2-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![AWS RDS](https://img.shields.io/badge/AWS_RDS-%23527FFF.svg?style=for-the-badge&logo=amazon-rds&logoColor=white) ![AWS CloudWatch](https://img.shields.io/badge/AWS_CloudWatch-%23FF4F8B.svg?style=for-the-badge&logo=amazon-cloudwatch&logoColor=white) |
| DevOps |  ![GitHub Actions](https://img.shields.io/badge/Github_Actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white) |
| Frontend | ![HTML5](https://img.shields.io/badge/HTML5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) ![jQuery](https://img.shields.io/badge/jQuery-%230769AD.svg?style=for-the-badge&logo=jquery&logoColor=white) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=thymeleaf&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) |
