import json
from deep_translator import GoogleTranslator
from pathlib import Path

# --- Cấu hình ---
input_file = r"C:\Users\nbhie\en.json"  # Đường dẫn file JSON gốc
output_languages = {
    "de": "german",
    "es": "spanish",
    "fr": "french",
    "hi": "hindi",
    "in": "indonesian",
    "pt": "portuguese"
}

# --- Đọc file JSON gốc ---
with open(input_file, "r", encoding="utf-8") as f:
    data = json.load(f)

# --- Hàm dịch ---
def translate_text(text, target_lang):
    if not text:
        return text
    try:
        return GoogleTranslator(source='auto', target=target_lang).translate(text)
    except Exception as e:
        print(f"Error translating '{text}' to {target_lang}: {e}")
        return text

# --- Tạo các file dịch ---
for lang_code, lang_name in output_languages.items():
    translated_data = []
    for item in data:
        translated_item = item.copy()
        # Dịch question
        translated_item['question'] = translate_text(item['question'], lang_name)
        # Dịch answerList
        translated_item['answerList'] = [translate_text(ans, lang_name) for ans in item.get('answerList', [])]
        translated_data.append(translated_item)
    
    # Ghi ra file JSON
    output_file = Path(f"{lang_code}.json")
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(translated_data, f, ensure_ascii=False, indent=4)
    
    print(f"Đã tạo file: {output_file}")
