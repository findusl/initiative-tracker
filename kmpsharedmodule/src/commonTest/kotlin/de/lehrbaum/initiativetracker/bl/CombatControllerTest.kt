package de.lehrbaum.initiativetracker.bl

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CombatControllerTest {
	private lateinit var combatController: CombatController

	@BeforeTest
	fun setUp() {
		combatController = CombatController()
	}

	@Test
	fun testAddCombatant() {
		val name = "Test Combatant"
		val initiative: Short = 10

		combatController.addCombatant(name, initiative)

		val addedCombatant = combatController.combatants.value.first()
		assertEquals(name, addedCombatant.name)
		assertEquals(initiative, addedCombatant.initiative)
	}

	@Test
	fun testUpdateCombatant() {
		val name = "Test Combatant"
		val initiative: Short = 10

		combatController.addCombatant(name, initiative)

		val updatedName = "Updated Combatant"
		val updatedInitiative: Short = 20

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
		val initiative: Short = 10

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
		val initiative: Short = 10

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
		val initiative: Short = 10

		combatController.addCombatant(name1, initiative)
		combatController.addCombatant(name2, initiative)

		combatController.prevTurn()
		assertEquals(1, combatController.activeCombatantIndex.value)
		combatController.prevTurn()
		assertEquals(0, combatController.activeCombatantIndex.value)
	}

	@Test
	fun `combatants should be sorted by initiative`() {
		// Arrange
		val controller = CombatController()

		// Add combatants out of order
		controller.addCombatant(initiative = 10)
		controller.addCombatant(initiative = 5)
		controller.addCombatant(initiative = 15)

		// Act
		val sortedCombatants = controller.combatants.value

		// Assert
		assertEquals(15, sortedCombatants[0].initiative)
		assertEquals(10, sortedCombatants[1].initiative)
		assertEquals(5, sortedCombatants[2].initiative)
	}

	@Test
	fun `combatants should be sorted by initiative and then by id if initiative is equal`() {
		// Arrange
		val controller = CombatController()

		// Add combatants with equal initiatives
		controller.addCombatant(initiative = 10)
		controller.addCombatant(initiative = 10)
		controller.addCombatant(initiative = 10)

		// Act
		val sortedCombatants = controller.combatants.value

		// Assert
		assertEquals(3, sortedCombatants.size)
		assertEquals(10, sortedCombatants[0].initiative)
		assertEquals(10, sortedCombatants[1].initiative)
		assertEquals(10, sortedCombatants[2].initiative)
		assertTrue(sortedCombatants[0].id < sortedCombatants[1].id)
		assertTrue(sortedCombatants[1].id < sortedCombatants[2].id)
	}

}
