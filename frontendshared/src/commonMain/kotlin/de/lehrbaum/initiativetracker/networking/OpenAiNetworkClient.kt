package de.lehrbaum.initiativetracker.networking

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
		val request = ChatCompletionRequest(
			model = ModelId("gpt-3.5-turbo"),
			messages = listOf(
				ChatMessage(
					role = ChatRole.System,
					content = """
						You help a Dungeon and Dragons dungeon master describe his creatures to the players.
						You are given the name of an existing Dungeon and Dragons creature. You provide a visual
						description of the creature using 3 words or less. The description shall not 
						include the original name of the creature.
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
		return openAi.chatCompletion(request).choices.firstOrNull()?.message?.content
	}

	@Suppress("unused")
	@DelicateCoroutinesApi
	@BetaOpenAI
	private fun example() {
		println("Test called")
		GlobalScope.launch {
			val models = openAi.models()
			println("Loaded models $models")
			/* Models:
Loaded models [Model(id=ModelId(id=text-davinci-001), created=1649364042, ownedBy=openai, permission=[ModelPermission(id=modelperm-CDlahk1RbkghXDjtxqzXoPNo, created=1690913868, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-curie-query-001), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-fNgpMH6ZEQulSq1CjzlfQuIe, created=1690864192, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=davinci), created=1649359874, ownedBy=openai, permission=[ModelPermission(id=modelperm-8s5tCuiXSr3zT00nLwZGyMpS, created=1690930152, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-babbage-001), created=1649364043, ownedBy=openai, permission=[ModelPermission(id=modelperm-YABzYWjC1kS6M2BnI6Fr9vuS, created=1690913878, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=curie-instruct-beta), created=1649364042, ownedBy=openai, permission=[ModelPermission(id=modelperm-4GYfzAdSMcJmQvF7bsw01UWw, created=1690863785, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-davinci-003), created=1669599635, ownedBy=openai-internal, permission=[ModelPermission(id=modelperm-a6niqBmW2JaGmo0fDO7FEt1n, created=1690930172, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-3.5-turbo-16k-0613), created=1685474247, ownedBy=openai, permission=[ModelPermission(id=modelperm-OPkkjVBEz20IWVysAi72govM, created=1691516018, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=davinci-similarity), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-XHJ9P2cvfDAl6Q6NABs6wD7G, created=1690864520, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=code-davinci-edit-001), created=1649880484, ownedBy=openai, permission=[ModelPermission(id=modelperm-T8Ie7SvlPyvtsDvPlfC8DftZ, created=1690915089, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-similarity-curie-001), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-ZQZGhVQCQSN4WC1wRJsFZfRL, created=1690864230, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-3.5-turbo-16k), created=1683758102, ownedBy=openai-internal, permission=[ModelPermission(id=modelperm-BYGQj1rUnLdY0H1Q5na1ONCB, created=1691516057, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-embedding-ada-002), created=1671217299, ownedBy=openai-internal, permission=[ModelPermission(id=modelperm-F3BGCNGb0ChzFesHIYjbNYUX, created=1690865307, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=ada-code-search-text), created=1651172510, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-jWFKGhnNYXhMIJuYYBe8zKoH, created=1690864242, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-ada-query-001), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-YO36k119sJYqPB8yHh737z8l, created=1690864529, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-4-0314), created=1687882410, ownedBy=openai, permission=[ModelPermission(id=modelperm-q9tcAQ9XhBjWdyxhWgFDYPaj, created=1691139822, allowCreateEngine=false, allowSampling=false, allowLogprobs=false, allowSearchIndices=false, allowView=false, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=babbage-search-query), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-o5hcKERXLlTSB0nfq8fPkAzK, created=1690864257, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=ada-similarity), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-Tz8CgePTpeDdl0q0mDxAseS4, created=1690864543, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-3.5-turbo), created=1677610602, ownedBy=openai, permission=[ModelPermission(id=modelperm-zy5TOjnE2zVaicIcKO9bQDgX, created=1690864883, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-4-0613), created=1686588896, ownedBy=openai, permission=[ModelPermission(id=modelperm-CWAN7JBmKfHDxOehfVPukiEL, created=1691615947, allowCreateEngine=false, allowSampling=false, allowLogprobs=false, allowSearchIndices=false, allowView=false, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-ada-doc-001), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-WQoo7GOoaleCrrerQ8ROIejy, created=1690864068, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-babbage-query-001), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-PHk5XyJIMEk88v14M2eEcJfE, created=1690864079, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=code-search-ada-code-001), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-lPJ8tQWzTuRpZpOjtRN4CjlP, created=1690864269, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=curie-search-document), created=1651172508, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-o3nt5yDhE7FpA8PtMlzGuW3k, created=1690864552, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-davinci-query-001), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-X2U9yi1RKudh1hGQ9CnPth2A, created=1690864090, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-curie-doc-001), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-7mOkCIwOIehlltLDPM1oSKN7, created=1690864279, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-3.5-turbo-0301), created=1677649963, ownedBy=openai, permission=[ModelPermission(id=modelperm-7WmfQzsq5FJ92UAnn24LduAN, created=1690842565, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=babbage-search-document), created=1651172510, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-FQiAIZXWHZ4yJl6b4X0JWpfw, created=1690864561, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=babbage-code-search-text), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-9AyTgRlbDLetEnvXKDgJvSvR, created=1690864101, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=davinci-instruct-beta), created=1649364042, ownedBy=openai, permission=[ModelPermission(id=modelperm-ZNpXjNy0lDniBWzpvi6w6wSU, created=1690842588, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=davinci-search-query), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-w5yjX7u1Hgz0jJFhPRB93n6I, created=1690864112, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-similarity-babbage-001), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-8p0vOyyD6xVDYv6XOC4EYIin, created=1690864583, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-davinci-002), created=1649880484, ownedBy=openai, permission=[ModelPermission(id=modelperm-Ao62Dd2uu76ec6Koq1ksR2rj, created=1690864376, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=code-search-babbage-text-001), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-uH251hsudZq0DqxtTcSYFTcD, created=1690864593, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=babbage), created=1649358449, ownedBy=openai, permission=[ModelPermission(id=modelperm-vZIqTaVk4K37PezAFVHAEW3H, created=1690943947, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-davinci-doc-001), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-sqcSr7AYu6WYtzWgysHg1zO4, created=1690864126, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=code-search-ada-text-001), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-1JbI0GFKw9luPgTJQut1uJNe, created=1690864601, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=ada-search-query), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-cBtmsjrTZIJUKgjS8G6uALKM, created=1690864138, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-similarity-ada-001), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-fSDlSniO72T5MvD6ieDRue0a, created=1690864457, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=whisper-1), created=1677532384, ownedBy=openai-internal, permission=[ModelPermission(id=modelperm-YfjOENC37iATh6VsjLLpYdeq, created=1691514055, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-4), created=1687882411, ownedBy=openai, permission=[ModelPermission(id=modelperm-ufulBjea4GV50lNUk7TL02lX, created=1691192558, allowCreateEngine=false, allowSampling=false, allowLogprobs=false, allowSearchIndices=false, allowView=false, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=ada-code-search-code), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-469coJJMBDffmGlbftht9QR7, created=1690864147, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=ada), created=1649357491, ownedBy=openai, permission=[ModelPermission(id=modelperm-mEzQ65zcTNX233nYMXVZjvmy, created=1690950776, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-davinci-edit-001), created=1649809179, ownedBy=openai, permission=[ModelPermission(id=modelperm-bwEWUtGiBcdX0p1D1ayafH8w, created=1690915020, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=davinci-search-document), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-1jEFSTL1yLUnTyI8TekKPGQF, created=1690864158, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=curie-search-query), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-fvYLh7mrZBoEXRa9teCq7ZsK, created=1690864488, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=babbage-similarity), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-XBmFjRKu34Qvm9Y8Vjg6si3V, created=1690864610, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=ada-search-document), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-jEtYYVTVutQ4BLh2DnGd9tJt, created=1690864171, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-ada-001), created=1649364042, ownedBy=openai, permission=[ModelPermission(id=modelperm-jRuB7xBCdj159SqaDmpPgeWO, created=1690915029, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-similarity-davinci-001), created=1651172505, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-CoAjJ7mSHeO28X7KowOnwvj9, created=1690864500, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=curie), created=1649359874, ownedBy=openai, permission=[ModelPermission(id=modelperm-0g6LBMO3cgUpTYzehqtF9G1i, created=1690950807, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=curie-similarity), created=1651172510, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-gSmuEPu9Q8KjQhJ5myLNKIIV, created=1690864620, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=gpt-3.5-turbo-0613), created=1686587434, ownedBy=openai, permission=[ModelPermission(id=modelperm-XIXH7QF7QM60DDcON9eaGFfk, created=1690842445, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=babbage-code-search-code), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-UdNutuGVhzb5EBzlkaztBdMH, created=1690864182, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=code-search-babbage-code-001), created=1651172507, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-0mO5qmzzKUVVVZ9MIHTnwjwK, created=1690864510, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-search-babbage-doc-001), created=1651172509, ownedBy=openai-dev, permission=[ModelPermission(id=modelperm-dvJNsLdOcnLbIYlRZRnfQAfX, created=1690864628, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=true, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)]),
Model(id=ModelId(id=text-curie-001), created=1649364043, ownedBy=openai, permission=[ModelPermission(id=modelperm-vcuXVPe8oCucYrY0hxBNBXRd, created=1690915039, allowCreateEngine=false, allowSampling=true, allowLogprobs=true, allowSearchIndices=false, allowView=true, allowFineTuning=false, organization=*, isBlocking=false)])]
			 */

			val chatCompletionRequest = ChatCompletionRequest(
				model = ModelId("gpt-3.5-turbo"),
				messages = listOf(
					ChatMessage(
						role = ChatRole.System,
						content = "You are a helpful assistant!"
					),
					ChatMessage(
						role = ChatRole.User,
						content = "Hello!"
					)
				)
			)
			val completions: Flow<ChatCompletionChunk> = openAi.chatCompletions(chatCompletionRequest)

			completions.collect {
				println("Got chat response $it")
				/*
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=ChatRole(role=assistant), content=, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content=Hello, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content=!, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content= How, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content= can, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content= I, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content= assist, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content= you, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content= today, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content=?, functionCall=null), finishReason=null)], usage=null)
Got chat response ChatCompletionChunk(id=chatcmpl-7lr4CniYY7VdQaxiOAYmEad8GaBYR, created=1691639760, model=ModelId(id=gpt-3.5-turbo-0613), choices=[ChatChunk(index=0, delta=ChatDelta(role=null, content=null, functionCall=null), finishReason=stop)], usage=null)
				 */
			}
		}
	}
}
