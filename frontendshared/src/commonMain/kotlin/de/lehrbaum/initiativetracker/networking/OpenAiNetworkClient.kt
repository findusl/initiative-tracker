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
	suspend fun suggestMonsterDescription(monsterType: String): String? {
		// TODO could provide the fluff from https://5e.tools/data/bestiary/fluff-bestiary-mm.json
		val request = ChatCompletionRequest(
			model = ModelId("gpt-3.5-turbo"),
			messages = listOf(
				ChatMessage(
					role = ChatRole.System,
					content = """
						You help a Dungeon and Dragons dungeon master describe his creatures to the players.
						You are given the name of an existing Dungeon and Dragons creature. 
						You describe what the creature looks like using 3 words or less. The description should include
						obvious features of the creature. The description may not include information about the creature
						that cannot be gained by looking at it. The description may not include the original name of the creature.
						The description should try to differentiate the creature from other creatures or objects if possible.
						For example, a Phase Spider could be described as a "blue glowing spider".
						If the creature is not known to you, you have to invent a description to the best of your abilities.
						Your response may never be more than 3 words!""".trimIndent()
				),
				ChatMessage(
					role = ChatRole.User,
					content = monsterType
				)
			)
		)
		val response = openAi.chatCompletion(request).choices.firstOrNull()?.message?.content ?: return null
		if (response.count { it.isWhitespace() } > 4) return null
		return response.removeSuffix(".")
	}
}
