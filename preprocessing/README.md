# 데이터 전처리 모듈

CSV 업데이트 시에만 스크립트 실행

### 1회 설정
```bash
cd preprocessing
python3 -m venv venv
source venv/bin/activate  # Mac/Linux
# Windows: venv\Scripts\activate

pip install -r requirements.txt
```

### 전처리 실행
```bash
python preprocess.py
```

출력: `../src/main/resources/data/processed_food.csv`

## 처리 내용
- 14,584개 음식 데이터 로딩
- 필요한 컬럼 선택 (메타데이터 + 영양성분)
- 결측치 처리 (칼로리 공식 활용, 카테고리 기반 추정)
- Flag 컬럼 생성 (결측 정보 보존)
