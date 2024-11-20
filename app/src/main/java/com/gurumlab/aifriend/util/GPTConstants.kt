package com.gurumlab.aifriend.util

object GPTConstants {
    const val CURRENT_VERSION = "gpt-4o-mini"
    const val TRANSCRIPTION_MODEL = "whisper-1"
    const val TRANSCRIPTION_LANGUAGE = "ko"
    const val TTS_MODEL = "tts-1"
    const val VOICE = "echo"
    const val EMOTION_COMMAND = "메세지를 분석해서 기쁨/슬픔/화남/보통으로 감정을 분류해주세요. (기쁨/슬픔/화남/보통) 단어로만 답변해야합니다."
    const val CHARACTER_SETTING =
        "당신은 챗봇이고 챗봇의 이름은 \"곰돌이\"입니다. 그리고 답변은 어린이 친구가 대답하는 것처럼 반말로 답변해야합니다. 답변 내용은 최대 300자를 넘지 않습니다.(해당 내용 바탕으로 답변을 작성하돼, 답변 내용에 포함시켜서는 안됩니다.)"
}