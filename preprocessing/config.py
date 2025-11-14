INPUT_FILE = '../src/main/resources/data/raw/전국통합식품영양성분정보_음식_표준데이터.csv'
OUTPUT_FILE = '../src/main/resources/data/processed/processed_food.csv'

METADATA_COLS = [
    '식품코드',
    '식품명',
    '에너지(kcal)',
    '식품대분류명',
    '식품중분류명',
    '식품소분류명',
    '영양성분함량기준량',
    '식품중량'
]

GROUP1_CORE = [
    '단백질(g)',
    '당류(g)',
    '탄수화물(g)',
    '지방(g)'
]

GROUP1_OPTIONAL = [
    '식이섬유(g)',
    '수분(g)'
]

GROUP2_COLS = [
    '나트륨(mg)',
    '포화지방산(g)'
]

GROUP3_COLS = [
    '칼륨(mg)',
    '비타민 D(μg)',
    '철(mg)',
    '티아민(mg)',
    '니아신(mg)',
    '비타민 A(μg RAE)',
    '비타민 C(mg)'
]

FLAG_COLS = GROUP1_OPTIONAL + GROUP3_COLS

ALL_SELECTED_COLS = METADATA_COLS + GROUP1_CORE + GROUP1_OPTIONAL + GROUP2_COLS + GROUP3_COLS
