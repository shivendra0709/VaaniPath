package com.vaanipath.arx.audio

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

import com.vaanipath.arx.model.AppLanguage

class AndroidOnDeviceSpeechEngine(
    context: Context
) : SpeechRecognitionEngine {

    private val appContext =
        context.applicationContext

    private val mainHandler =
        Handler(Looper.getMainLooper())

    private var speechRecognizer: SpeechRecognizer? = null

    /*
     * Used so that an old recognition callback
     * cannot affect a new attempt.
     */
    private var requestId = 0L

    /*
     * Prevents endless fallback attempts.
     */
    private var usingStandardRecognizer = false

    // =========================================================
    // AVAILABILITY
    // =========================================================

    override fun isAvailable(): Boolean {

        /*
         * We no longer require an ON-DEVICE recognizer.
         *
         * Android may have a normal speech recognition service
         * even when the on-device service is unavailable.
         */
        return SpeechRecognizer.isRecognitionAvailable(
            appContext
        )
    }

    // =========================================================
    // START LISTENING
    // =========================================================

    override fun startListening(
        language: AppLanguage,
        onPartialResult: (String) -> Unit,
        onFinalResult: (SpeechRecognitionResult) -> Unit,
        onError: (String) -> Unit
    ) {

        mainHandler.post {

            requestId++

            val currentRequest =
                requestId

            usingStandardRecognizer = false

            startRecognition(
                language = language,
                localeIndex = 0,
                currentRequest = currentRequest,
                allowStandardFallback = true,
                onPartialResult = onPartialResult,
                onFinalResult = onFinalResult,
                onError = onError
            )
        }
    }

    // =========================================================
    // START RECOGNITION
    // =========================================================

    private fun startRecognition(
        language: AppLanguage,
        localeIndex: Int,
        currentRequest: Long,
        allowStandardFallback: Boolean,
        onPartialResult: (String) -> Unit,
        onFinalResult: (SpeechRecognitionResult) -> Unit,
        onError: (String) -> Unit
    ) {

        /*
         * Ignore an old request.
         */
        if (currentRequest != requestId) {
            return
        }

        /*
         * Destroy previous recognizer.
         */
        destroyRecognizer()

        /*
         * Get locale candidates.
         */
        val locales =
            localeCandidates(language)

        /*
         * No more locale candidates.
         */
        if (localeIndex >= locales.size) {

            onError(
                "Speech recognition language is unavailable on this device."
            )

            return
        }

        val locale =
            locales[localeIndex]

        try {

            /*
             * Prefer on-device recognition when available.
             *
             * If it isn't available, use Android's normal
             * SpeechRecognizer.
             */
            val canUseOnDevice =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        SpeechRecognizer.isOnDeviceRecognitionAvailable(
                            appContext
                        )

            val useOnDevice =
                canUseOnDevice &&
                        !usingStandardRecognizer

            speechRecognizer =
                if (useOnDevice) {

                    SpeechRecognizer
                        .createOnDeviceSpeechRecognizer(
                            appContext
                        )

                } else {

                    SpeechRecognizer
                        .createSpeechRecognizer(
                            appContext
                        )
                }

            // =================================================
            // LISTENER
            // =================================================

            speechRecognizer?.setRecognitionListener(

                object : RecognitionListener {

                    override fun onReadyForSpeech(
                        params: Bundle?
                    ) {
                    }

                    override fun onBeginningOfSpeech() {
                    }

                    override fun onRmsChanged(
                        rmsdB: Float
                    ) {
                    }

                    override fun onBufferReceived(
                        buffer: ByteArray?
                    ) {
                    }

                    override fun onEndOfSpeech() {
                    }

                    // =========================================
                    // ERROR
                    // =========================================

                    override fun onError(
                        error: Int
                    ) {

                        /*
                         * Ignore callbacks from an old
                         * recognition request.
                         */
                        if (currentRequest != requestId) {
                            return
                        }

                        /*
                         * If the selected language is not currently
                         * available, try fallback strategies.
                         */
                        if (isLanguageUnavailable(error)) {

                            /*
                             * First fallback:
                             *
                             * If we were using on-device recognition,
                             * switch to Android's standard recognizer.
                             */
                            if (
                                !usingStandardRecognizer &&
                                allowStandardFallback
                            ) {

                                usingStandardRecognizer = true

                                mainHandler.post {

                                    startRecognition(
                                        language = language,
                                        localeIndex = localeIndex,
                                        currentRequest = currentRequest,
                                        allowStandardFallback = false,
                                        onPartialResult = onPartialResult,
                                        onFinalResult = onFinalResult,
                                        onError = onError
                                    )
                                }

                                return
                            }

                            /*
                             * Second fallback:
                             *
                             * Try the next locale.
                             */
                            if (
                                localeIndex + 1 <
                                locales.size
                            ) {

                                mainHandler.post {

                                    startRecognition(
                                        language = language,
                                        localeIndex = localeIndex + 1,
                                        currentRequest = currentRequest,
                                        allowStandardFallback = allowStandardFallback,
                                        onPartialResult = onPartialResult,
                                        onFinalResult = onFinalResult,
                                        onError = onError
                                    )
                                }

                                return
                            }
                        }

                        onError(
                            errorMessage(error)
                        )
                    }

                    // =========================================
                    // FINAL RESULT
                    // =========================================

                    override fun onResults(
                        results: Bundle?
                    ) {

                        if (currentRequest != requestId) {
                            return
                        }

                        val matches =
                            results?.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION
                            )

                        val text =
                            matches
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()

                        val confidenceValues =
                            results?.getFloatArray(
                                SpeechRecognizer.CONFIDENCE_SCORES
                            )

                        val confidence =
                            confidenceValues
                                ?.firstOrNull()
                                ?.takeIf {
                                    it >= 0f
                                }

                        onFinalResult(
                            SpeechRecognitionResult(
                                text = text,
                                confidence = confidence
                            )
                        )
                    }

                    // =========================================
                    // PARTIAL RESULTS
                    // =========================================

                    override fun onPartialResults(
                        partialResults: Bundle?
                    ) {

                        if (currentRequest != requestId) {
                            return
                        }

                        val matches =
                            partialResults
                                ?.getStringArrayList(
                                    SpeechRecognizer.RESULTS_RECOGNITION
                                )

                        val text =
                            matches
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()

                        if (text.isNotBlank()) {

                            onPartialResult(text)
                        }
                    }

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?
                    ) {
                    }
                }
            )

            // =================================================
            // RECOGNIZER INTENT
            // =================================================

            val intent =
                Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                ).apply {

                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE,
                        locale
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                        true
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_MAX_RESULTS,
                        3
                    )

                    /*
                     * Prefer offline processing when the
                     * recognizer supports it.
                     *
                     * Standard recognizer may still use its
                     * available service when offline is not possible.
                     */
                    putExtra(
                        RecognizerIntent.EXTRA_PREFER_OFFLINE,
                        true
                    )
                }

            // =================================================
            // START
            // =================================================

            speechRecognizer?.startListening(
                intent
            )

        } catch (e: Exception) {

            e.printStackTrace()

            /*
             * If on-device creation fails, try the
             * standard recognizer.
             */
            if (
                !usingStandardRecognizer &&
                allowStandardFallback
            ) {

                usingStandardRecognizer = true

                mainHandler.post {

                    startRecognition(
                        language = language,
                        localeIndex = localeIndex,
                        currentRequest = currentRequest,
                        allowStandardFallback = false,
                        onPartialResult = onPartialResult,
                        onFinalResult = onFinalResult,
                        onError = onError
                    )
                }

            } else {

                onError(
                    e.message
                        ?: "Could not start speech recognition"
                )

                destroyRecognizer()
            }
        }
    }

    // =========================================================
    // STOP
    // =========================================================

    override fun stopListening() {

        mainHandler.post {

            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // =========================================================
    // CANCEL
    // =========================================================

    override fun cancel() {

        mainHandler.post {

            /*
             * Invalidate callbacks.
             */
            requestId++

            try {
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            destroyRecognizer()
        }
    }

    // =========================================================
    // RELEASE
    // =========================================================

    override fun release() {

        mainHandler.post {

            requestId++

            destroyRecognizer()
        }
    }

    // =========================================================
    // LANGUAGE CANDIDATES
    // =========================================================

    private fun localeCandidates(
        language: AppLanguage
    ): List<String> {

        return when (language) {

            /*
             * Indian English first because VaaniPath is
             * being developed for Indian users.
             */
            AppLanguage.ENGLISH -> {

                listOf(
                    "en-IN",
                    "en-US",
                    "en"
                )
            }

            AppLanguage.HINDI -> {

                listOf(
                    "hi-IN",
                    "hi"
                )
            }
        }
    }

    // =========================================================
    // LANGUAGE ERROR CHECK
    // =========================================================

    private fun isLanguageUnavailable(
        error: Int
    ): Boolean {

        if (
            error ==
            SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED
        ) {
            return true
        }

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            if (
                error ==
                SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE
            ) {
                return true
            }
        }

        return false
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private fun errorMessage(
        error: Int
    ): String {

        return when (error) {

            SpeechRecognizer.ERROR_AUDIO ->
                "Audio recording error"

            SpeechRecognizer.ERROR_CLIENT ->
                "Speech recognition client error"

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                "Microphone permission denied"

            SpeechRecognizer.ERROR_NETWORK ->
                "Speech recognition network error"

            SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                "Speech recognition network timeout"

            SpeechRecognizer.ERROR_NO_MATCH ->
                "No speech recognized"

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                "Speech recognizer is busy"

            SpeechRecognizer.ERROR_SERVER ->
                "Speech recognition server error"

            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                "No speech detected"

            SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ->
                "This language is not supported by the speech service"

            else -> {

                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S &&
                    error ==
                    SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE
                ) {

                    "This language is not currently available. Trying another speech service."

                } else {

                    "Speech recognition error: $error"
                }
            }
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    private fun destroyRecognizer() {

        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {
        }

        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        }

        speechRecognizer = null
    }
}