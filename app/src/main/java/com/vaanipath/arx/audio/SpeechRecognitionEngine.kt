package com.vaanipath.arx.audio

import com.vaanipath.arx.model.AppLanguage

data class SpeechRecognitionResult(
    val text: String = "",
    val confidence: Float? = null
)

interface SpeechRecognitionEngine {

    fun isAvailable(): Boolean

    fun startListening(
        language: AppLanguage,
        onPartialResult: (String) -> Unit,
        onFinalResult: (SpeechRecognitionResult) -> Unit,
        onError: (String) -> Unit
    )

    fun stopListening()

    fun cancel()

    fun release()
}