package de.lehrbaum.initiativetracker.view

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun Context.requestSessionIdInput(): Int {
	val builder = AlertDialog.Builder(this)
	builder.setTitle("Please provide the SessionId")
	val input = EditText(this)
	input.inputType = InputType.TYPE_CLASS_NUMBER
	builder.setView(input)

	return suspendCancellableCoroutine { continuation ->
		builder.setPositiveButton("OK") { dialog, _ ->
			dialog.dismiss() // TODO handle invalid values
			val sessionId = input.text.toString().toInt()
			continuation.resume(sessionId)
		}
		builder.setNegativeButton("Cancel") { dialog, _ ->
			dialog.cancel()
			continuation.cancel()
		}
		builder.setOnDismissListener {
			continuation.cancel()
		}

		builder.show()
	}
}
