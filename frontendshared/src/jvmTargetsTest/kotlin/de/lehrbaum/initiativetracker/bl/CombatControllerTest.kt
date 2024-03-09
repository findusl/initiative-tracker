package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.AOEDecision.KEEP
import de.lehrbaum.initiativetracker.bl.AOEDecision.OVERWRITE_FAILURE
import de.lehrbaum.initiativetracker.bl.AOEDecision.OVERWRITE_IGNORE
import de.lehrbaum.initiativetracker.bl.AOEDecision.OVERWRITE_SUCCESS
import de.lehrbaum.initiativetracker.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.bl.model.AoeOptions
import de.lehrbaum.initiativetracker.bl.model.SaveDC
import de.lehrbaum.initiativetracker.bl.model.SavingThrow
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("INLINE_FROM_HIGHER_PLATFORM") // KTIJ-18375
class CombatControllerTest {
	private val seed = 12345L
	private lateinit var combatController: CombatController
	private val generalSettingsRepository = mockk<GeneralSettingsRepository>()
	private val confirmationRequester = mockk<ConfirmationRequester>()

	@BeforeTest
	fun setUp() {
		every { generalSettingsRepository.installationId } returns 1
		combatController = CombatController(generalSettingsRepository, confirmationRequester, seed)
	}

	@Test
	fun testAddCombatant() {
		val name = "Test Combatant"
		val initiative = 10

		combatController.addCombatant(name, initiative)

		val addedCombatant = combatController.combatants.value.first()
		assertEquals(name, addedCombatant.name)
		assertEquals(initiative, addedCombatant.initiative)
	}

	@Test
	fun testUpdateCombatant() {
		val name = "Test Combatant"
		val initiative = 10

		combatController.addCombatant(name, initiative)

		val updatedName = "Updated Combatant"
		val updatedInitiative = 20

		val updatedCombatant = combatController.combatants.value.first().copy(
			name = updatedName,
			initiative = updatedInitiative
		)
		combatController.updateCombatant(updatedCombatant)

		val retrievedCombatant = combatController.combatants.value.first()
		assertEquals(updatedName, retrievedCombatant.name)
		assertEquals(updatedInitiative, retrievedCombatant.initiative)
	}

	@Test
	fun testDeleteCombatant() {
		val name = "Test Combatant"
		val initiative = 10

		combatController.addCombatant(name, initiative)

		val addedCombatant = combatController.combatants.value.first()

		combatController.deleteCombatant(addedCombatant.id)

		val remainingCombatants = combatController.combatants.value
		assertEquals(0, remainingCombatants.size)
	}

	@Test
	fun testNextTurn() {
		val name1 = "Test Combatant 1"
		val name2 = "Test Combatant 2"
		val initiative = 10

		combatController.addCombatant(name1, initiative)
		combatController.addCombatant(name2, initiative)

		combatController.nextTurn()
		assertEquals(1, combatController.activeCombatantIndex.value)
		combatController.nextTurn()
		assertEquals(0, combatController.activeCombatantIndex.value)
	}

	@Test
	fun testPrevTurn() {
		val name1 = "Test Combatant 1"
		val name2 = "Test Combatant 2"
		val initiative = 10

		combatController.addCombatant(name1, initiative)
		combatController.addCombatant(name2, initiative)

		combatController.prevTurn()
		assertEquals(1, combatController.activeCombatantIndex.value)
		combatController.prevTurn()
		assertEquals(0, combatController.activeCombatantIndex.value)
	}

	@Test
	fun `combatants should be sorted by initiative`() {

		// Add combatants out of order
		combatController.addCombatant(initiative = 10)
		combatController.addCombatant(initiative = 5)
		combatController.addCombatant(initiative = 15)

		// Act
		val sortedCombatants = combatController.combatants.value

		// Assert
		assertEquals(15, sortedCombatants[0].initiative)
		assertEquals(10, sortedCombatants[1].initiative)
		assertEquals(5, sortedCombatants[2].initiative)
	}

	@Test
	fun `combatants should be sorted by initiative and then by id if initiative is equal`() {

		// Add combatants with equal initiatives
		combatController.addCombatant(initiative = 10)
		combatController.addCombatant(initiative = 10)
		combatController.addCombatant(initiative = 10)

		// Act
		val sortedCombatants = combatController.combatants.value

		// Assert
		assertEquals(3, sortedCombatants.size)
		assertEquals(10, sortedCombatants[0].initiative)
		assertEquals(10, sortedCombatants[1].initiative)
		assertEquals(10, sortedCombatants[2].initiative)
		assertTrue(sortedCombatants[0].id < sortedCombatants[1].id)
		assertTrue(sortedCombatants[1].id < sortedCombatants[2].id)
	}

	@Test
	fun `Damage is applied correctly when handling aoe`() {
		val initialHp = 20
		val damage = 10
		val combatant0 = combatController.addCombatant(CombatantModel(ownerId = UserId(0), name = "", currentHp = initialHp, wisSave = 0))
		val combatant2 = combatController.addCombatant(CombatantModel(ownerId = UserId(0), name = "", currentHp = initialHp, wisSave = 0))
		val combatant3 = combatController.addCombatant(CombatantModel(ownerId = UserId(0), name = "", currentHp = initialHp, wisSave = 0))
		val combatant4 = combatController.addCombatant(CombatantModel(ownerId = UserId(0), name = "", currentHp = initialHp, wisSave = 0))
		coEvery { confirmationRequester.confirmAoe(any(), any(), any()) } returns
			mapOf(combatant0 to KEEP, combatant2 to OVERWRITE_FAILURE, combatant3 to OVERWRITE_SUCCESS, combatant4 to OVERWRITE_IGNORE)
		val options = AoeOptions(damage, SaveDC(SavingThrow.WIS, 20), true)

		val result = runBlocking {
			combatController.handleAoeRequest(options, listOf(combatant0.id), UserId(0))
		}

		assertTrue(result)
		assertEquals(initialHp - damage, combatController.combatants.value[0].currentHp)
		assertEquals(initialHp - damage, combatController.combatants.value[1].currentHp)
		assertEquals(initialHp - (damage / 2), combatController.combatants.value[2].currentHp)
		assertEquals(initialHp, combatController.combatants.value[3].currentHp)
	}

	@Test
	fun `Save bonus is applied correctly when handling aoe`() {
		val combatant = combatController.addCombatant(CombatantModel(ownerId = UserId(0), name = "", currentHp = 20, wisSave = 20))
		coEvery { confirmationRequester.confirmAoe(any(), any(), any()) } returns mapOf(combatant to KEEP)
		val options = AoeOptions(10, SaveDC(SavingThrow.WIS, 20), true)

		val result = runBlocking {
			combatController.handleAoeRequest(options, listOf(combatant.id), UserId(0))
		}

		assertTrue(result)
		assertEquals(15, combatController.combatants.value.first().currentHp)
	}

}
