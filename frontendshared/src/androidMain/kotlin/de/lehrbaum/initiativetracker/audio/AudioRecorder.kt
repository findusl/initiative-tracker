package de.lehrbaum.initiativetracker.audio

import okio.Buffer

actual class AudioRecorder: AutoCloseable {
	actual val isAvailable = false
    actual fun startRecording() {
		TODO("Not yet implemented")
    }

	/**
	 * @return A Buffer of a WAV formatted byte stream
	 */
    actual fun stopRecording(): Result<Buffer>  {
        TODO("Not yet implemented")
    }

	actual override fun close() {
		TODO("Not yet implemented")
	}
}