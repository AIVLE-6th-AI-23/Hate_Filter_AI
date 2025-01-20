from .text_preprocessor import TextPreprocessor
from .image_preprocessor import ImagePreprocessor
from .video_preprocessor import VideoPreprocessor

class PreprocessorFactory:
    @staticmethod
    def get_preprocessor(data_type: str):
        preprocessors = {
            'text': TextPreprocessor,
            'image': ImagePreprocessor,
            'video': VideoPreprocessor
        }
        return preprocessors.get(data_type.lower())()
    
__all__ = [
    'TextPreprocessor',
    'ImagePreprocessor',
    'VideoPreprocessor',
    'PreprocessorFactory'
]