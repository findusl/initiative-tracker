package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.audio.AudioRecorder
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.networking.flatMap
import io.github.aakira.napier.Napier

private const val TAG = "AudioCommandController"

class AudioCommandController(
	private val combatController: CombatController
) {
	private var audioRecorder = AudioRecorder()
	private val openAiNetworkClientProvider = GlobalInstances.openAiNetworkClientProvider

	private val combatants: Iterable<CombatantModel>
		get() = combatController.combatants.value

	val isAvailable: Boolean
		get() = audioRecorder.isAvailable && openAiNetworkClientProvider.isAvailable()

	fun startRecordingCommand() {
		audioRecorder.startRecording()
	}

	suspend fun processRecording(): Result<CombatCommand> {
		val client = openAiNetworkClientProvider.getClient()
			?: return Result.failure(IllegalStateException("OpenAI is unavailable"))

		val result = audioRecorder.stopRecording()
			.flatMap { recording ->
				client.interpretSpokenCombatCommand(recording, combatants)
			}.onSuccess {
				Napier.i("Interpreted command as $it", tag = TAG)
			}

		// free up the resources. This can most likely be improved for re-use but not a concern right now
		audioRecorder.close()
		audioRecorder = AudioRecorder()
		return result
	}

	fun cancelRecording() {
		audioRecorder.close()
		audioRecorder = AudioRecorder()
	}

}
