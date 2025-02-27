import threading

READER_LANGUAGES = {
    "ko": ["ko", "en"],
    "ru": ["ru", "en"],
    "vi": ["vi", "en"],
    "fr": ["fr", "en"],
    "ja": ["ja", "en"],
    "zh": ["ch_sim", "en"],
    "ar": ["ar", "en"],
    "hi": ["hi", "en"],
}

_readers = None
_readers_lock = threading.Lock()


def get_readers():
    global _readers
    if _readers is None:
        with _readers_lock:
            if _readers is None:
                import easyocr

                _readers = {
                    language: easyocr.Reader(reader_languages)
                    for language, reader_languages in READER_LANGUAGES.items()
                }
    return _readers


def try_all_readers(image):
    best_result = {"text": "", "confidence": 0, "lang": ""}

    for lang, reader in get_readers().items():
        text_results = reader.readtext(image)
        if text_results:
            confidence = sum(result[2] for result in text_results) / len(text_results)
            extracted_text = " ".join([result[1] for result in text_results])

            if confidence > best_result["confidence"]:
                best_result = {
                    "text": extracted_text,
                    "confidence": confidence,
                    "lang": lang,
                }

    return best_result
