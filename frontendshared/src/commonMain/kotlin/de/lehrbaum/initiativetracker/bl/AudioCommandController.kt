package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.audio.AudioRecorder
import io.github.aakira.napier.Napier

private const val TAG = "AudioCommandController"

class AudioCommandController {
	private var audioRecorder = AudioRecorder()
	private val openAiNetworkClient = GlobalInstances.openAiNetworkClient

	val isAvailable: Boolean
		get() = audioRecorder.isAvailable && openAiNetworkClient != null

	fun startRecordingCommand() {
		audioRecorder.startRecording()
	}

	suspend fun finishRecordingCommand() {
		val recording = audioRecorder.stopRecording()
		val transcription = openAiNetworkClient?.interpretSpokenCommand(recording)
		transcription?.let { Napier.i("Result $transcription", tag = TAG) }
		// free up the resources. This can most likely be improved but not a concern right now
		audioRecorder.close()
		audioRecorder = AudioRecorder()
	}

}
