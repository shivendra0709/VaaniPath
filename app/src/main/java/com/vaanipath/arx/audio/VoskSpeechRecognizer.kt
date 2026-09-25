package com.vaanipath.arx.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class VoskSpeechRecognizer(
    private val context: Context
) {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    private var latestText = ""

    // Used to invalidate old callbacks.
    private var sessionId = 0L

    private val sampleRate = 16000f

    // =========================================================
    // INITIALIZE
    // =========================================================

    @Synchronized
    fun initialize(): Boolean {
        return try {

            val modelDirectory =
                File(context.filesDir, "model-en")

            if (!modelDirectory.exists()) {
                copyAssetFolder(
                    "model-en",
                    modelDirectory
                )
            }

            try {
                model?.close()
            } catch (_: Exception) {
            }

            model =
                Model(modelDirectory.absolutePath)

            true

        } catch (e: Exception) {

            e.printStackTrace()
            false
        }
    }

    // =========================================================
    // START LISTENING
    // =========================================================

    @Synchronized
    fun startListening(
        expectedWord: String,
        callback: (String) -> Unit
    ) {

        // expectedWord is retained for compatibility.
        // We intentionally do NOT use it as a grammar.
        // This keeps every question on the same recognizer setup.

        // Completely invalidate/close any previous session.
        stopListening()

        // -----------------------------------------------------
        // PERMISSION
        // -----------------------------------------------------

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback("ERROR: Microphone permission denied")
            return
        }

        // -----------------------------------------------------
        // MODEL
        // -----------------------------------------------------

        if (model == null) {

            if (!initialize()) {
                callback("ERROR: Vosk model could not be loaded")
                return
            }
        }

        try {

            latestText = ""

            // -------------------------------------------------
            // NEW SESSION
            // -------------------------------------------------

            sessionId++
            val currentSession = sessionId

            // -------------------------------------------------
            // NEW RECOGNIZER
            // -------------------------------------------------

            val newRecognizer =
                Recognizer(
                    model,
                    sampleRate
                )

            recognizer = newRecognizer

            // -------------------------------------------------
            // NEW SPEECH SERVICE
            // -------------------------------------------------

            val newSpeechService =
                SpeechService(
                    newRecognizer,
                    sampleRate
                )

            speechService = newSpeechService

            // -------------------------------------------------
            // LISTENER
            // -------------------------------------------------

            val listener =
                object : RecognitionListener {

                    override fun onPartialResult(
                        hypothesis: String
                    ) {

                        // Ignore callbacks from old sessions.
                        if (currentSession != sessionId) {
                            return
                        }

                        val text =
                            extractPartialText(hypothesis)

                        if (text.isNotBlank()) {

                            latestText = text
                            callback(text)
                        }
                    }

                    override fun onResult(
                        hypothesis: String
                    ) {

                        if (currentSession != sessionId) {
                            return
                        }

                        val text =
                            extractText(hypothesis)

                        if (text.isNotBlank()) {

                            latestText = text
                            callback(text)
                        }
                    }

                    override fun onFinalResult(
                        hypothesis: String
                    ) {

                        if (currentSession != sessionId) {
                            return
                        }

                        val text =
                            extractText(hypothesis)

                        if (text.isNotBlank()) {

                            latestText = text
                            callback(text)
                        }
                    }

                    override fun onError(
                        exception: Exception
                    ) {

                        if (currentSession != sessionId) {
                            return
                        }

                        exception.printStackTrace()

                        callback(
                            "ERROR: ${
                                exception.message
                                    ?: "Vosk recognition error"
                            }"
                        )
                    }

                    override fun onTimeout() {
                        // AssessmentScreen controls the 5-second timeout.
                    }
                }

            // -------------------------------------------------
            // START
            // -------------------------------------------------

            newSpeechService.startListening(listener)

        } catch (e: Exception) {

            e.printStackTrace()

            callback(
                "ERROR: ${
                    e.message
                        ?: "Could not start Vosk"
                }"
            )

            cleanupWithoutInvalidating()
        }
    }

    // =========================================================
    // STOP LISTENING
    // =========================================================

    @Synchronized
    fun stopListening(): String {

        /*
         * Invalidate callbacks FIRST.
         *
         * Any callback arriving after this belongs to
         * an old session and will be ignored.
         */
        sessionId++

        val service = speechService
        val currentRecognizer = recognizer

        if (
            service == null &&
            currentRecognizer == null
        ) {
            return latestText.trim()
        }

        // -----------------------------------------------------
        // STOP AUDIO CAPTURE
        // -----------------------------------------------------

        try {
            service?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*
         * Give SpeechService a short moment to finish processing.
         */
        try {
            Thread.sleep(100)
        } catch (_: InterruptedException) {
        }

        // -----------------------------------------------------
        // FINAL RESULT
        // -----------------------------------------------------

        try {

            val finalJson =
                currentRecognizer?.getFinalResult()

            val finalText =
                extractText(finalJson)

            if (finalText.isNotBlank()) {
                latestText = finalText
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // -----------------------------------------------------
        // SHUTDOWN
        // -----------------------------------------------------

        try {
            service?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            currentRecognizer?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // -----------------------------------------------------
        // CLEAR REFERENCES
        // -----------------------------------------------------

        speechService = null
        recognizer = null

        return latestText.trim()
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    private fun cleanupWithoutInvalidating() {

        try {
            speechService?.shutdown()
        } catch (_: Exception) {
        }

        try {
            recognizer?.close()
        } catch (_: Exception) {
        }

        speechService = null
        recognizer = null
    }

    // =========================================================
    // FINAL TEXT
    // =========================================================

    private fun extractText(
        json: String?
    ): String {

        if (json.isNullOrBlank()) {
            return ""
        }

        return try {

            JSONObject(json)
                .optString("text", "")
                .trim()

        } catch (_: Exception) {

            ""
        }
    }

    // =========================================================
    // PARTIAL TEXT
    // =========================================================

    private fun extractPartialText(
        json: String?
    ): String {

        if (json.isNullOrBlank()) {
            return ""
        }

        return try {

            JSONObject(json)
                .optString("partial", "")
                .trim()

        } catch (_: Exception) {

            ""
        }
    }

    // =========================================================
    // COPY ASSET FOLDER
    // =========================================================

    private fun copyAssetFolder(
        assetFolder: String,
        destination: File
    ) {

        if (!destination.exists()) {
            destination.mkdirs()
        }

        val files =
            context.assets.list(assetFolder) ?: return

        for (fileName in files) {

            val assetPath =
                "$assetFolder/$fileName"

            val destinationFile =
                File(destination, fileName)

            val children =
                context.assets.list(assetPath)

            if (
                children != null &&
                children.isNotEmpty()
            ) {

                copyAssetFolder(
                    assetPath,
                    destinationFile
                )

            } else {

                copyAssetFile(
                    assetPath,
                    destinationFile
                )
            }
        }
    }

    // =========================================================
    // COPY ASSET FILE
    // =========================================================

    private fun copyAssetFile(
        assetPath: String,
        destination: File
    ) {

        destination.parentFile?.mkdirs()

        val inputStream: InputStream =
            context.assets.open(assetPath)

        val outputStream =
            FileOutputStream(destination)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    // =========================================================
    // RELEASE
    // =========================================================

    @Synchronized
    fun release() {

        // Invalidate all callbacks.
        sessionId++

        val service = speechService
        val currentRecognizer = recognizer

        speechService = null
        recognizer = null

        try {
            service?.stop()
        } catch (_: Exception) {
        }

        try {
            service?.shutdown()
        } catch (_: Exception) {
        }

        try {
            currentRecognizer?.close()
        } catch (_: Exception) {
        }

        try {
            model?.close()
        } catch (_: Exception) {
        }

        model = null
    }
}