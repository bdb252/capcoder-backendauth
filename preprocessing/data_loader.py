import pandas as pd
from config import INPUT_FILE, ALL_SELECTED_COLS

def load_nutrition_data():
    df = pd.read_csv(INPUT_FILE, encoding='euc-kr')
    df = df[ALL_SELECTED_COLS]
    return df
