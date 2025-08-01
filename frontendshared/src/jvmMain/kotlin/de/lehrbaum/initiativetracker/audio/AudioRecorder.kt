package de.lehrbaum.initiativetracker.audio

import io.github.aakira.napier.Napier
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.CancellationException
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.io.Buffer
import kotlinx.io.asOutputStream

private const val TAG = "AudioRecorder"

actual class AudioRecorder : AutoCloseable, CoroutineScope {
	override val coroutineContext: Job = SupervisorJob()
	private lateinit var line: TargetDataLine
	private lateinit var format: AudioFormat
	private var _isAvailable = false
	actual val isAvailable: Boolean
		get() = _isAvailable
	private var recordingStream: ByteArrayOutputStream? = null
	private var isRecording = false

	init {
		determineLineAndFormat()
	}

	private fun determineLineAndFormat() {
		val mixerInfos = AudioSystem.getMixerInfo()

		for (mixerInfo in mixerInfos) {
			val mixer = AudioSystem.getMixer(mixerInfo)
			val lineInfos = mixer.targetLineInfo

			for (lineInfo in lineInfos) {
				if (lineInfo is DataLine.Info) {
					val line = mixer.getLine(lineInfo) as? TargetDataLine
					val formats = lineInfo.formats

					val format = formats.firstOrNull { it.sampleRate.toInt() != AudioSystem.NOT_SPECIFIED }
					if (line != null && format != null) {
						this.line = line
						this.format = format
						_isAvailable = true
						return
					}
				}
			}
		}
	}

	// Start recording audio
	actual fun startRecording() {
		isRecording = true
		if (!line.isOpen) {
			line.open(format)
		}
		line.start()
		recordingStream = ByteArrayOutputStream()
		Thread {
			try {
				val buffer = ByteArray(1024)
				recordingStream?.let { out ->
					while (isRecording) {
						val bytesRead = line.read(buffer, 0, buffer.size)
						if (bytesRead > 0 && isRecording) {
							out.write(buffer, 0, bytesRead)
						}
					}
				}
			} catch (e: IOException) {
				Napier.w("Exception while recording audio", e, tag = TAG)
			} finally {
				line.close()
			}
		}.start()
	}

	// Stop recording and return the audio data
	actual fun stopRecording(): Result<Buffer> {
		if (!isRecording) return Result.failure(IllegalStateException("AudioRecorder is not recording"))
		isRecording = false
		line.stop()
		line.flush()
		val buffer = recordingStream ?: return Result.failure(IllegalStateException("RecordingStream is null"))

		recordingStream = null
		return Result.success(convertToWavBuffer(buffer))
	}

	private fun convertToWavBuffer(rawBuffer: ByteArrayOutputStream): Buffer {
		val audioBytes = rawBuffer.toByteArray()
		val bais = ByteArrayInputStream(audioBytes)
		val audioInputStream = AudioInputStream(bais, format, audioBytes.size.toLong() / format.frameSize)
		val out = Buffer()
		AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out.asOutputStream())
		return out
	}

	actual override fun close() {
		line.close()
		recordingStream?.close()
		recordingStream = null
		coroutineContext.cancel(CancellationException("AudioRecorder closed"))
	}
}
