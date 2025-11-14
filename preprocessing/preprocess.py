from data_loader import load_nutrition_data
from missing_value_handler import handle_all_missing_values
from config import OUTPUT_FILE

def main():
    print("=" * 80)
    print("식품 영양 데이터 전처리 시작")
    print("=" * 80)

    print("\n[1/3] CSV 파일 로딩 중...")
    df = load_nutrition_data()
    print(f"완료: {len(df)}개 행, {len(df.columns)}개 컬럼")

    print("\n[2/3] 결측치 처리 중...")
    df = handle_all_missing_values(df)
    print("완료")

    print(f"\n[3/3] 결과 저장 중: {OUTPUT_FILE}")
    df.to_csv(OUTPUT_FILE, index=False, encoding='utf-8')
    print("완료")

    print("\n" + "=" * 80)
    print("전처리 완료")
    print(f"출력: {OUTPUT_FILE}")
    print(f"총 {len(df)}개 행, {len(df.columns)}개 컬럼")
    print("=" * 80)

if __name__ == '__main__':
    main()
