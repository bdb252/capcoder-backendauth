import pandas as pd
import numpy as np
from config import GROUP2_COLS, FLAG_COLS

def calculate_category_stats(df):
    stats = {}
    for category in df['식품대분류명'].unique():
        cat_df = df[df['식품대분류명'] == category]
        stats[category] = {
            '지방_중앙값': cat_df['지방(g)'].median(),
            '탄수화물_중앙값': cat_df['탄수화물(g)'].median()
        }
    return stats

def fill_fat_carb_single_missing(row):
    E = row['에너지(kcal)']
    P = row['단백질(g)']
    F = row['지방(g)']
    C = row['탄수화물(g)']

    remaining_cal = E - 4 * P

    if pd.isna(F) and pd.notna(C):
        F = (remaining_cal - 4 * C) / 9
        return max(0, F), C

    if pd.isna(C) and pd.notna(F):
        C = (remaining_cal - 9 * F) / 4
        return F, max(0, C)

    return F, C

def fill_fat_carb_both_missing(row, category_stats):
    E = row['에너지(kcal)']
    P = row['단백질(g)']
    category = row['식품대분류명']

    remaining_cal = E - 4 * P

    median_fat = category_stats[category]['지방_중앙값']
    median_carb = category_stats[category]['탄수화물_중앙값']

    if pd.isna(median_fat) or median_fat == 0:
        if pd.isna(median_carb) or median_carb == 0:
            fat = remaining_cal * 0.2 / 9 if remaining_cal > 0 else 0
            carb = remaining_cal * 0.8 / 4 if remaining_cal > 0 else 0
        else:
            fat = 0
            carb = remaining_cal / 4 if remaining_cal > 0 else 0
    elif pd.isna(median_carb) or median_carb == 0:
        fat = remaining_cal / 9 if remaining_cal > 0 else 0
        carb = 0
    else:
        median_cal = 9 * median_fat + 4 * median_carb
        if median_cal > 0:
            scale = remaining_cal / median_cal
            fat = median_fat * scale
            carb = median_carb * scale
        else:
            fat = 0
            carb = 0

    return max(0, fat), max(0, carb)

def handle_group1_core_missing(df):
    category_stats = calculate_category_stats(df)

    for idx, row in df.iterrows():
        F = row['지방(g)']
        C = row['탄수화물(g)']

        if (pd.isna(F) and pd.notna(C)) or (pd.notna(F) and pd.isna(C)):
            new_F, new_C = fill_fat_carb_single_missing(row)
            df.at[idx, '지방(g)'] = new_F
            df.at[idx, '탄수화물(g)'] = new_C
        elif pd.isna(F) and pd.isna(C):
            new_F, new_C = fill_fat_carb_both_missing(row, category_stats)
            df.at[idx, '지방(g)'] = new_F
            df.at[idx, '탄수화물(g)'] = new_C

    df['당류(g)'] = df['당류(g)'].fillna(0)

    return df

def handle_group2_missing(df):
    for col in GROUP2_COLS:
        df[col] = df[col].fillna(0)
    return df

def handle_optional_with_flags(df):
    for col in FLAG_COLS:
        flag_col = f"{col}_is_missing"
        df[flag_col] = df[col].isna().astype(int)
        df[col] = df[col].fillna(0)
    return df

def handle_all_missing_values(df):
    print("\n처리 전 결측치:")
    missing_before = df.isnull().sum()
    print(missing_before[missing_before > 0])

    df = handle_group1_core_missing(df)
    df = handle_group2_missing(df)
    df = handle_optional_with_flags(df)

    print("\n처리 후 결측치:")
    missing_after = df.isnull().sum()
    print(missing_after[missing_after > 0])

    return df
