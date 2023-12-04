from PIL import Image
import pytesseract

def extract_text_from_image(image_path):
    # 画像を開く
    img = Image.open(image_path)

    # Tesseract OCRを使用して文字を抽出
    text = pytesseract.image_to_string(img)

    return text

if __name__ == "__main__":
    # 画像ファイルのパス
    image_path = "path/to/your/image.jpg"

    # 画像から文字を抽出
    extracted_text = extract_text_from_image(image_path)

    # 抽出された文字を出力
    print("Extracted Text:")
    print(extracted_text)
