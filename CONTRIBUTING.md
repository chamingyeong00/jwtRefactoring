# 🤝 Yummy-Pass 협업 가이드

## 브랜치 전략
- `master`: 최종 제출용
- `develop`: 통합 개발 브랜치
- `feat/{이슈번호}-{기능요약}`: 기능 단위 작업 브랜치

---

## 작업 흐름

### 1️⃣ 이슈 작성
- GitHub Issues에 작업할 이슈 생성  
- 생성된 이슈 번호 확인 (예: #7)

### 2️⃣ 브랜치 생성
- **항상 develop 최신을 기준으로 작업 시작**  
- `feat/{이슈번호}-{기능요약}` 브랜치 생성  

```bash
git checkout develop       # develop 브랜치로 이동
git pull                   # 최신 develop 코드 받아오기
git checkout -b feature/7-home-ui  # 새 feature 브랜치 생성 및 이동
```

### 3️⃣ 기능 구현 및 커밋  
- `[커밋타입/#이슈번호] - 커밋내용`  
- [feat/#7] - 홈 화면 레이아웃 추가

### 4️⃣ 원격 브랜치 Push
```bash
git push origin feature/7-home-ui 	#feature/{이슈번호}-{기능요약}
```

### 5️⃣ 코드 확인 후 Merge  
- GitHub로 이동 → Pull Request 생성
- PR 작성 시:  
	• base: develop  
	• compare: feature/7-home-ui
- Merge 버튼 클릭하여 develop 브랜치에 병합

### 6️⃣ 최종 제출 시 `develop` → `master` merge
```bash
  git checkout master
  git pull
  git merge develop
  git push origin master
```

## 커밋 컨벤션

형식: `[커밋타입/#이슈번호] - 커밋내용`

| 타입 | 설명 |
|---|---|
| feat | 신규 기능 개발 |
| fix | 버그 수정 |
| docs | 문서(주석) 수정
| style | 코드 스타일, 포맷팅에 대한 수정 |
| refactor | 코드 리팩토링 |
| test | 테스트 코드 추가/수정 |
| chore | 패키지 매니저 수정, 그 외 기타 수정 ex) .gitignore |

## 이슈 라벨 가이드

| 라벨 | 설명 |
|---|---|
| Type : Feature | 신규 기능 개발 |
| Type : Bug | 버그 수정 |
| Type : Refactor | 코드 리팩토링 |
| Type : Chore | 유지보수 (설정, 환경, 패키지 등) |
| For : API | 서버 API 작업 |
| For : Entity/DB | 데이터베이스 작업 |
| For : Html&css | 스타일링 (마크업 & CSS) |
