package com.vaanipath.arx.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.vaanipath.arx.model.AppLanguage
import java.util.Locale

class VaaniSpeech(
    context: Context
) {

    private val appContext = context.applicationContext

    private var initialized = false

    private var pendingText: String? = null
    private var pendingLanguage: AppLanguage? = null

    private val textToSpeech =
        TextToSpeech(appContext) { status ->

            if (status == TextToSpeech.SUCCESS) {

                initialized = true

                val text = pendingText
                val language = pendingLanguage

                if (text != null && language != null) {
                    speakNow(
                        text,
                        language
                    )
                }

                pendingText = null
                pendingLanguage = null
            }
        }

    fun speak(
        text: String,
        language: AppLanguage
    ) {

        if (!initialized) {

            pendingText = text
            pendingLanguage = language

            return
        }

        speakNow(
            text,
            language
        )
    }

    private fun speakNow(
        text: String,
        language: AppLanguage
    ) {

        val locale = when (language) {

            AppLanguage.HINDI ->
                Locale("hi", "IN")

            AppLanguage.ENGLISH ->
                Locale("en", "IN")
        }

        val result =
            textToSpeech.setLanguage(locale)

        if (
            result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {

            textToSpeech.language =
                Locale.US
        }

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            "vaanipath_${System.currentTimeMillis()}"
        )
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun shutdown() {

        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}