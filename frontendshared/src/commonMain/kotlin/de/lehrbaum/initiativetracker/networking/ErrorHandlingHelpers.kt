package de.lehrbaum.initiativetracker.networking

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.io.IOException

/**
 * Run catching with a nested Result
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, R> T.runCatchingNested(block: T.() -> Result<R>): Result<R> {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
	return try {
		block()
	} catch (e: Throwable) {
		Result.failure(e)
	}
}

@OptIn(ExperimentalContracts::class)
inline fun <T, R> Result<T>.flatMap(block: (T) -> Result<R>): Result<R> {
	contract {
		callsInPlace(block, InvocationKind.AT_MOST_ONCE)
	}
	val value = getOrElse {
		return Result.failure(it)
	}
	return block(value)
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Result<T>.filter(description: String, condition: (T) -> Boolean): Result<T> {
	contract {
		callsInPlace(condition, InvocationKind.AT_MOST_ONCE)
	}
	val value = getOrElse {
		return Result.failure(it)
	}
	return if (condition(value)) this else Result.failure(RuntimeException(description))
}

fun <R> Iterable<Result<R>>.filterIsSuccess(): List<R> = filter { it.isSuccess }.map { it.getOrThrow() }

suspend inline fun <reified T> HttpResponse.bodyOrFailure(): Result<T> =
	if (status.isSuccess()) Result.success(body()) else Result.failure(IOException("ResponseStatus $status"))
