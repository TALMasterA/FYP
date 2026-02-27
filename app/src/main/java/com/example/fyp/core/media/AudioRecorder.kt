package com.example.fyp.core

import android.media.AudioRecord
import android.util.Log
import java.io.ByteArrayOutputStream

object AudioRecorder {

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null

    @Volatile
    var isRecording: Boolean = false
        private set

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

    fun stopIfRecording() {
        if (isRecording) {
            stop(null)
        }
    }
}