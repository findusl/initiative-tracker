package de.lehrbaum.initiativetracker.ui.model.edit

import androidx.compose.runtime.mutableStateOf
import de.lehrbaum.initiativetracker.bl.CombatantModel
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel

class EditCombatantModelImpl(
    CombatantViewModel: CombatantViewModel,
    private val onSave: (CombatantModel) -> Unit,
    private val onCancel: () -> Unit,
) : EditCombatantModel {
    private val id = CombatantViewModel.id
    override val name = mutableStateOf(CombatantViewModel.name)
    override val nameError = mutableStateOf(false)
    override val initiativeString = mutableStateOf(CombatantViewModel.initiativeString)
    override val initiativeError = mutableStateOf(false)
    override val maxHpString = mutableStateOf(CombatantViewModel.maxHp.toString())
    override val maxHpError = mutableStateOf(false)
    override val currentHpString = mutableStateOf(CombatantViewModel.currentHp.toString())
    override val currentHpError = mutableStateOf(false)

    override fun onSavePressed() {
        val initiative = initiativeString.value.toIntOrNull()
        initiativeError.value = initiative == null
        val maxHp = maxHpString.value.toIntOrNull()
        maxHpError.value = maxHp == null
        val currentHp = currentHpString.value.toIntOrNull()
        currentHpError.value = currentHp == null
        val name = this.name.value
        nameError.value = name.isBlank()
        if (initiative!=null && name.isNotBlank() && maxHp!=null && currentHp!=null) {
            onSave(CombatantModel(id, name, initiative, maxHp, currentHp))
        }
    }

    override fun onCancelPressed() {
        onCancel()
    }
}
