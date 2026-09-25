package com.vaanipath.arx.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder(
    private val context: Context
) {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): File {

        val directory = File(
            context.filesDir,
            "recordings"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(
            directory,
            "attempt_${System.currentTimeMillis()}.m4a"
        )

        outputFile = file

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder?.apply {

            setAudioSource(
                MediaRecorder.AudioSource.MIC
            )

            setOutputFormat(
                MediaRecorder.OutputFormat.MPEG_4
            )

            setAudioEncoder(
                MediaRecorder.AudioEncoder.AAC
            )

            setAudioEncodingBitRate(128000)

            setAudioSamplingRate(44100)

            setOutputFile(
                file.absolutePath
            )

            prepare()
            start()
        }

        return file
    }

    fun stopRecording(): File? {

        recorder?.let {

            try {
                it.stop()
            } catch (_: RuntimeException) {
            }

            it.reset()
            it.release()
        }

        recorder = null

        return outputFile
    }

    fun isRecording(): Boolean {
        return recorder != null
    }

    fun release() {

        recorder?.let {

            try {
                it.stop()
            } catch (_: RuntimeException) {
            }

            it.reset()
            it.release()
        }

        recorder = null
    }
}