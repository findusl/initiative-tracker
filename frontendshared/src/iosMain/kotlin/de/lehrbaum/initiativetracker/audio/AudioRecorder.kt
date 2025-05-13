package de.lehrbaum.initiativetracker.audio

import kotlinx.io.Buffer

actual class AudioRecorder {
	actual val isAvailable = false
    actual fun startRecording() {
		TODO("Not yet implemented")
    }

    actual fun stopRecording(): Result<Buffer> {
        TODO("Not yet implemented")
    }

	actual fun close() {
		TODO("Not yet implemented")
	}
}