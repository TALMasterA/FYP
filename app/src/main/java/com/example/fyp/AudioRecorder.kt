package com.example.fyp

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.ByteArrayOutputStream

object AudioRecorder {

    private const val SAMPLE_RATE = 16000
    private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private const val CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null

    @Volatile
    var isRecording: Boolean = false
        private set

    @SuppressLint("MissingPermission")
    fun start(stream: ByteArrayOutputStream) {
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING)
        if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e("AudioRecorder", "Invalid parameters for AudioRecord.")
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_MASK,
            ENCODING,
            bufferSize
        )

        stream.reset()
        isRecording = true
        audioRecord?.startRecording()

        recordingThread = Thread {
            val data = ByteArray(bufferSize)
            while (isRecording) {
                val read = audioRecord?.read(data, 0, bufferSize) ?: 0
                if (read > 0) stream.write(data, 0, read)
            }
        }.apply { start() }

        Log.d("AudioRecorder", "Recording started.")
    }

    fun stop(stream: ByteArrayOutputStream?): ByteArray? {
        if (isRecording) {
            isRecording = false
            try {
                recordingThread?.join()
            } catch (e: InterruptedException) {
                Log.e("AudioRecorder", "Recording thread interrupted", e)
            }
            recordingThread = null

            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            Log.d("AudioRecorder", "Recording stopped and resources released.")
        }
        return stream?.toByteArray()
    }
}