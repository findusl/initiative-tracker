package de.lehrbaum.initiativetracker.networking

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlin.time.Duration.Companion.seconds

class OpenAiNetworkClient(token: String) {
	private val config = OpenAIConfig(
		token = token,
		timeout = Timeout(socket = 60.seconds),
		logging = LoggingConfig(),
		// additional configurations...
	)
	private val openAi = OpenAI(config)

	@BetaOpenAI
	suspend fun suggestMonsterName(monsterType: String): String? {
		// TODO could provide the fluff from https://5e.tools/data/bestiary/fluff-bestiary-mm.json
		val request = ChatCompletionRequest(
			model = ModelId("gpt-3.5-turbo"),
			messages = listOf(
				ChatMessage(
					role = ChatRole.System,
					content = nameSuggestionSystemPrompt
				),
				ChatMessage(
					role = ChatRole.User,
					content = monsterType
				)
			)
		)
		val response = openAi.chatCompletion(request).choices.firstOrNull()?.message?.content ?: return null
		if (response.count { it.isWhitespace() } > 4) return null
		return response.removeSuffix(".").capitalizeWords()
	}

	private fun String.capitalizeWords(): String {
		return this.split(" ").joinToString(" ") { it.capitalizeWord() }
	}

	private fun String.capitalizeWord(): String =
		replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private val nameSuggestionSystemPrompt = """
You help a Dungeon and Dragons dungeon master describe his creatures to the players.
You are given the name of an existing Dungeon and Dragons creature.
You describe what the creature looks like using 3 words or less. 
The description may not include information about the creature that is not readily apparent.
In particular it may not include information about abilities of the creature! To prevent this avoid using verbs!
The description can use the name of a well known creature, in that case it should include an adjective to differentiate
from that creature. Avoid using the actual name of the creature.
If the creature is not known to you, you have to invent a description to the best of your abilities.
Your response may never be more than 3 words!
""".trimIndent()
