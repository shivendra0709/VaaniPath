package com.vaanipath.arx.audio

import android.content.Context
import com.vaanipath.arx.model.AppLanguage

class SpeechRecognitionManager(
    context: Context
) {

    private val engine: SpeechRecognitionEngine =
        AndroidOnDeviceSpeechEngine(context)

    fun isAvailable(): Boolean {
        return engine.isAvailable()
    }

    fun startListening(
        language: AppLanguage,
        onPartialResult: (String) -> Unit,
        onFinalResult: (SpeechRecognitionResult) -> Unit,
        onError: (String) -> Unit
    ) {
        engine.startListening(
            language = language,
            onPartialResult = onPartialResult,
            onFinalResult = onFinalResult,
            onError = onError
        )
    }

    fun stopListening() {
        engine.stopListening()
    }

    fun cancel() {
        engine.cancel()
    }

    fun release() {
        engine.release()
    }
}