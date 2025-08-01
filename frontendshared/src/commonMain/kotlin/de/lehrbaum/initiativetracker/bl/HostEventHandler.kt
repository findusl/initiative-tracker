package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.dtos.commands.HostCommand
import de.lehrbaum.initiativetracker.dtos.commands.ServerToHostCommand
import io.github.aakira.napier.Napier

class HostEventHandler(private val combatController: CombatController) {
	suspend fun handleEvent(incoming: ServerToHostCommand): HostCommand {
		Napier.d("Received command $incoming")
		when (incoming) {
			is ServerToHostCommand.AddCombatant -> {
				combatController.addCombatant(incoming.combatant)
				return HostCommand.CommandCompleted(true)
			}

			is ServerToHostCommand.EditCombatant -> {
				combatController.updateCombatant(incoming.combatant)
				return HostCommand.CommandCompleted(true)
			}

			is ServerToHostCommand.DamageCombatant -> {
				val result = combatController.handleDamageCombatantRequest(incoming.targetId, incoming.damage, incoming.ownerId)
				return HostCommand.CommandCompleted(result)
			}

			is ServerToHostCommand.FinishTurn -> {
				val result = combatController.handleFinishTurnRequest(incoming.activeCombatantIndex)
				return HostCommand.CommandCompleted(result)
			}
		}
	}
}
