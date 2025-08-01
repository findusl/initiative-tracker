package de.lehrbaum.initiativetracker.audio

import kotlinx.io.Buffer

// TASK This would probably be better as an interface and expect fun getAudioRecorder
// This way there would be no need to implement stubs on all platforms for every function, just for one
expect class AudioRecorder() {
	val isAvailable: Boolean

	fun startRecording()

	fun stopRecording(): Result<Buffer>

	fun close()
}
