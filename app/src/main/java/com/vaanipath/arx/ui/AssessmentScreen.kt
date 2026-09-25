package com.vaanipath.arx.ui

import android.Manifest
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.core.content.ContextCompat

import com.vaanipath.arx.audio.SpeechRecognitionManager
import com.vaanipath.arx.audio.VaaniSpeech
import com.vaanipath.arx.model.AppLanguage
import com.vaanipath.arx.model.AssessmentLevel

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AssessmentScreen(
    language: AppLanguage,
    level: AssessmentLevel,
    onBackClick: () -> Unit,
    onComplete: () -> Unit
) {

    val context = LocalContext.current

    // =========================================================
    // TEXT TO SPEECH
    // =========================================================

    val speech =
        remember {
            VaaniSpeech(context)
        }

    // =========================================================
    // SPEECH RECOGNITION
    // =========================================================

    val speechManager =
        remember {
            SpeechRecognitionManager(context)
        }

    val scope =
        rememberCoroutineScope()

    // =========================================================
    // STATES
    // =========================================================

    var isSpeechReady by remember {
        mutableStateOf(false)
    }

    var isListening by remember {
        mutableStateOf(false)
    }

    var recognizedText by remember {
        mutableStateOf("")
    }

    var attemptCompleted by remember {
        mutableStateOf(false)
    }

    var currentQuestion by remember {
        mutableIntStateOf(0)
    }

    var recordingJob by remember {
        mutableStateOf<Job?>(null)
    }

    /*
     * This number identifies the currently active
     * question/attempt.
     *
     * Old callbacks cannot modify a newer question.
     */
    var questionSession by remember {
        mutableIntStateOf(0)
    }

    var hasMicPermission by remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // =========================================================
    // CHECK SPEECH ENGINE
    // =========================================================

    LaunchedEffect(Unit) {

        isSpeechReady =
            speechManager.isAvailable()
    }

    // =========================================================
    // MICROPHONE PERMISSION
    // =========================================================

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasMicPermission =
                granted
        }

    // =========================================================
    // QUESTIONS
    // =========================================================

    val questions =

        when (language) {

            // -------------------------------------------------
            // ENGLISH
            // -------------------------------------------------

            AppLanguage.ENGLISH -> {

                when (level) {

                    AssessmentLevel.LEVEL_1 ->

                        listOf(
                            "Sun",
                            "Cat",
                            "Book",
                            "Tree",
                            "Apple"
                        )

                    AssessmentLevel.LEVEL_2 ->

                        listOf(
                            "School",
                            "Garden",
                            "Window",
                            "Rabbit",
                            "Flower"
                        )

                    AssessmentLevel.LEVEL_3 ->

                        listOf(
                            "Ram goes home.",
                            "The child eats an apple.",
                            "Sita reads a book."
                        )
                }
            }

            // -------------------------------------------------
            // HINDI
            // -------------------------------------------------

            AppLanguage.HINDI -> {

                when (level) {

                    AssessmentLevel.LEVEL_1 ->

                        listOf(
                            "कमल",
                            "घर",
                            "आम",
                            "पानी",
                            "मछली"
                        )

                    AssessmentLevel.LEVEL_2 ->

                        listOf(
                            "किताब",
                            "सड़क",
                            "बादल",
                            "खिड़की",
                            "चिड़िया"
                        )

                    AssessmentLevel.LEVEL_3 ->

                        listOf(
                            "राम घर जाता है।",
                            "बच्चा आम खाता है।",
                            "सीमा किताब पढ़ती है।"
                        )
                }
            }
        }

    // =========================================================
    // CURRENT QUESTION TEXT
    // =========================================================

    val currentText =
        questions[currentQuestion]

    // =========================================================
    // INSTRUCTION
    // =========================================================

    val instruction =

        when (language) {

            AppLanguage.ENGLISH -> {

                when (level) {

                    AssessmentLevel.LEVEL_1 ->
                        "Read the word carefully and say it."

                    AssessmentLevel.LEVEL_2 ->
                        "Read the word carefully and speak clearly."

                    AssessmentLevel.LEVEL_3 ->
                        "Read the sentence carefully and say it."
                }
            }

            AppLanguage.HINDI -> {

                when (level) {

                    AssessmentLevel.LEVEL_1 ->
                        "शब्द को ध्यान से पढ़ो और बोलो।"

                    AssessmentLevel.LEVEL_2 ->
                        "शब्द को ध्यान से पढ़ो और साफ बोलो।"

                    AssessmentLevel.LEVEL_3 ->
                        "वाक्य को ध्यान से पढ़ो और बोलो।"
                }
            }
        }

    // =========================================================
    // QUESTION CHANGED
    // =========================================================

    LaunchedEffect(
        language,
        level,
        currentQuestion
    ) {

        /*
         * Invalidate all previous callbacks.
         */
        questionSession++

        /*
         * Cancel old timer.
         */
        recordingJob?.cancel()
        recordingJob = null

        /*
         * Reset UI.
         */
        isListening = false
        recognizedText = ""
        attemptCompleted = false

        /*
         * Cancel any previous speech recognition.
         *
         * IMPORTANT:
         * This does NOT start recognition.
         */
        speechManager.cancel()

        /*
         * Only TTS starts automatically.
         */
        speech.speak(
            instruction,
            language
        )
    }

    // =========================================================
    // START ONE ATTEMPT
    // =========================================================

    fun startAttempt() {

        if (!isSpeechReady) {

            recognizedText =
                "On-device speech recognition is not available."

            return
        }

        if (isListening) {
            return
        }

        // -----------------------------------------------------
        // MIC PERMISSION
        // -----------------------------------------------------

        if (!hasMicPermission) {

            permissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )

            return
        }

        // -----------------------------------------------------
        // CANCEL OLD TIMER
        // -----------------------------------------------------

        recordingJob?.cancel()
        recordingJob = null

        // -----------------------------------------------------
        // NEW ATTEMPT ID
        // -----------------------------------------------------

        questionSession++

        val thisAttempt =
            questionSession

        // -----------------------------------------------------
        // RESET UI
        // -----------------------------------------------------

        recognizedText = ""
        attemptCompleted = false
        isListening = true

        // =====================================================
        // START ANDROID SPEECH RECOGNITION
        // =====================================================

        speechManager.startListening(

            language = language,

            // -------------------------------------------------
            // PARTIAL RESULT
            // -------------------------------------------------

            onPartialResult = { text ->

                if (
                    thisAttempt == questionSession &&
                    text.isNotBlank()
                ) {

                    recognizedText =
                        text
                }
            },

            // -------------------------------------------------
            // FINAL RESULT
            // -------------------------------------------------

            onFinalResult = { result ->

                if (thisAttempt == questionSession) {

                    isListening = false

                    if (
                        result.text.isNotBlank()
                    ) {

                        recognizedText =
                            result.text

                    } else if (
                        recognizedText.isBlank()
                    ) {

                        recognizedText =
                            "No speech recognized"
                    }

                    attemptCompleted =
                        true

                    recordingJob?.cancel()
                    recordingJob = null
                }
            },

            // -------------------------------------------------
            // ERROR
            // -------------------------------------------------

            onError = { error ->

                if (thisAttempt == questionSession) {

                    isListening = false

                    recognizedText =
                        error

                    /*
                     * Do not freeze the assessment
                     * when recognition fails.
                     */
                    attemptCompleted =
                        true

                    recordingJob?.cancel()
                    recordingJob = null
                }
            }
        )

        // =====================================================
        // 5 SECOND SAFETY TIMER
        // =====================================================

        recordingJob =
            scope.launch {

                delay(5000)

                /*
                 * Timer belongs to an old attempt.
                 */
                if (
                    thisAttempt != questionSession
                ) {

                    return@launch
                }

                if (!isListening) {

                    return@launch
                }

                /*
                 * Ask SpeechRecognizer to finish.
                 *
                 * Final result will arrive through
                 * onFinalResult().
                 */
                speechManager.stopListening()

                /*
                 * Give recognizer time to send
                 * the final result.
                 */
                delay(500)

                if (
                    thisAttempt != questionSession
                ) {

                    return@launch
                }

                /*
                 * Safety fallback.
                 */
                if (isListening) {

                    isListening = false

                    if (
                        recognizedText.isBlank()
                    ) {

                        recognizedText =
                            "No speech recognized"
                    }

                    attemptCompleted =
                        true
                }

                recordingJob = null
            }
    }

    // =========================================================
    // MANUAL STOP
    // =========================================================

    fun manualStop() {

        if (!isListening) {
            return
        }

        /*
         * Invalidate the current attempt.
         */
        questionSession++

        val stoppedAttempt =
            questionSession

        recordingJob?.cancel()
        recordingJob = null

        /*
         * Tell Android recognizer to finish.
         */
        speechManager.stopListening()

        /*
         * Give it time to send final result.
         */
        scope.launch {

            delay(500)

            if (
                stoppedAttempt != questionSession
            ) {

                return@launch
            }

            isListening = false

            if (
                recognizedText.isBlank()
            ) {

                recognizedText =
                    "No speech recognized"
            }

            attemptCompleted =
                true
        }
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    DisposableEffect(Unit) {

        onDispose {

            questionSession++

            recordingJob?.cancel()
            recordingJob = null

            speechManager.release()

            speech.shutdown()
        }
    }

    // =========================================================
    // UI
    // =========================================================

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        // =====================================================
        // LEVEL
        // =====================================================

        Text(

            text =

                when (language) {

                    AppLanguage.ENGLISH -> {

                        when (level) {

                            AssessmentLevel.LEVEL_1 ->
                                "Level 1 • Start"

                            AssessmentLevel.LEVEL_2 ->
                                "Level 2 • Practice"

                            AssessmentLevel.LEVEL_3 ->
                                "Level 3 • Challenge"
                        }
                    }

                    AppLanguage.HINDI -> {

                        when (level) {

                            AssessmentLevel.LEVEL_1 ->
                                "स्तर 1 • शुरुआत"

                            AssessmentLevel.LEVEL_2 ->
                                "स्तर 2 • अभ्यास"

                            AssessmentLevel.LEVEL_3 ->
                                "स्तर 3 • चुनौती"
                        }
                    }
                },

            style =
                MaterialTheme
                    .typography
                    .headlineSmall,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.height(16.dp)
        )

        // =====================================================
        // QUESTION NUMBER
        // =====================================================

        Text(

            text =

                if (
                    language ==
                    AppLanguage.ENGLISH
                ) {

                    "Question ${currentQuestion + 1} / ${questions.size}"

                } else {

                    "प्रश्न ${currentQuestion + 1} / ${questions.size}"
                }
        )

        Spacer(
            Modifier.height(24.dp)
        )

        // =====================================================
        // WORD / SENTENCE CARD
        // =====================================================

        Card(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(180.dp)
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(

                    text =
                        currentText,

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            Modifier.height(18.dp)
        )

        // =====================================================
        // INSTRUCTION
        // =====================================================

        Text(

            text =
                instruction,

            style =
                MaterialTheme
                    .typography
                    .bodyLarge,

            fontWeight =
                FontWeight.Medium
        )

        Spacer(
            Modifier.height(18.dp)
        )

        // =====================================================
        // LISTEN
        // =====================================================

        OutlinedButton(

            enabled =
                !isListening,

            onClick = {

                speech.speak(
                    currentText,
                    language
                )
            }
        ) {

            Text(

                if (
                    language ==
                    AppLanguage.ENGLISH
                ) {

                    "🔊 Listen"

                } else {

                    "🔊 सुनो"
                }
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        // =====================================================
        // SPEAK
        // =====================================================

        Button(

            enabled =
                isSpeechReady &&
                        !isListening,

            onClick = {

                startAttempt()
            }
        ) {

            Text(

                if (
                    language ==
                    AppLanguage.ENGLISH
                ) {

                    "🎙 Speak"

                } else {

                    "🎙 बोलें"
                }
            )
        }

        // =====================================================
        // LISTENING STATE
        // =====================================================

        if (isListening) {

            Spacer(
                Modifier.height(12.dp)
            )

            Text(

                text =

                    if (
                        language ==
                        AppLanguage.ENGLISH
                    ) {

                        "🎙 Listening... Speak now"

                    } else {

                        "🎙 सुन रहा हूँ... अब बोलो"
                    },

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            OutlinedButton(

                onClick = {

                    manualStop()
                }
            ) {

                Text(

                    if (
                        language ==
                        AppLanguage.ENGLISH
                    ) {

                        "⏹ Stop Listening"

                    } else {

                        "⏹ रोकें"
                    }
                )
            }
        }

        // =====================================================
        // RECOGNIZED TEXT
        // =====================================================

        if (
            recognizedText.isNotBlank()
        ) {

            Spacer(
                Modifier.height(14.dp)
            )

            Text(

                text =

                    if (
                        language ==
                        AppLanguage.ENGLISH
                    ) {

                        "You said: $recognizedText"

                    } else {

                        "आपने कहा: $recognizedText"
                    },

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                fontWeight =
                    FontWeight.Bold
            )
        }

        // =====================================================
        // TRY AGAIN
        // =====================================================

        if (
            attemptCompleted &&
            !isListening
        ) {

            Spacer(
                Modifier.height(10.dp)
            )

            OutlinedButton(

                onClick = {

                    startAttempt()
                }
            ) {

                Text(

                    if (
                        language ==
                        AppLanguage.ENGLISH
                    ) {

                        "🔄 Try Again"

                    } else {

                        "🔄 फिर से बोलें"
                    }
                )
            }
        }

        Spacer(
            Modifier.height(12.dp)
        )

        // =====================================================
        // NEXT / COMPLETE
        // =====================================================

        Button(

            enabled =
                attemptCompleted &&
                        !isListening,

            onClick = {

                /*
                 * Invalidate current attempt.
                 */
                questionSession++

                /*
                 * Cancel timer.
                 */
                recordingJob?.cancel()
                recordingJob = null

                /*
                 * Cancel Android speech recognition.
                 */
                speechManager.cancel()

                /*
                 * Reset UI.
                 */
                isListening = false
                recognizedText = ""
                attemptCompleted = false

                /*
                 * Move to next question.
                 */
                if (
                    currentQuestion <
                    questions.lastIndex
                ) {

                    currentQuestion++

                } else {

                    onComplete()
                }
            }
        ) {

            Text(

                if (
                    currentQuestion ==
                    questions.lastIndex
                ) {

                    if (
                        language ==
                        AppLanguage.ENGLISH
                    ) {

                        "Complete"

                    } else {

                        "पूरा करें"
                    }

                } else {

                    if (
                        language ==
                        AppLanguage.ENGLISH
                    ) {

                        "Next"

                    } else {

                        "अगला"
                    }
                }
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        // =====================================================
        // BACK
        // =====================================================

        OutlinedButton(

            enabled =
                !isListening,

            onClick = {

                questionSession++

                recordingJob?.cancel()
                recordingJob = null

                speechManager.cancel()

                isListening = false

                onBackClick()
            }
        ) {

            Text(

                if (
                    language ==
                    AppLanguage.ENGLISH
                ) {

                    "Back"

                } else {

                    "वापस"
                }
            )
        }
    }
}