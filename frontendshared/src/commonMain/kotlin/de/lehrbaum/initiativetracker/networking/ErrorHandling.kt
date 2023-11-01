package de.lehrbaum.initiativetracker.networking

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException

/**
 * Run catching with a nested Result
 */
inline fun <T, R> T.runCatchingNested(block: T.() -> Result<R>): Result<R> {
	return try {
		block()
	} catch (e: Throwable) {
		Result.failure(e)
	}
}

suspend inline fun <reified T> HttpResponse.bodyOrFailure(): Result<T> =
	if (status.isSuccess()) Result.success(body()) else Result.failure(IOException("ResponseStatus $status"))
