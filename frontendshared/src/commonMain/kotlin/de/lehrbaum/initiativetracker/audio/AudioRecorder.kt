package de.lehrbaum.initiativetracker.audio

import okio.Buffer

expect class AudioRecorder() {
	val isAvailable: Boolean
	fun startRecording()
	fun stopRecording(): Buffer

	fun close()
}