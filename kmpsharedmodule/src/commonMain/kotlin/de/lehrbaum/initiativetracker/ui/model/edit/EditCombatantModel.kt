package de.lehrbaum.initiativetracker.ui.model.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable

@Stable
interface EditCombatantModel {
    val name: MutableState<String>
    val nameError: MutableState<Boolean>
    val initiativeString: MutableState<String>
    val initiativeError: MutableState<Boolean>
    val maxHpString: MutableState<String>
    val maxHpError: MutableState<Boolean>
    val currentHpString: MutableState<String>
    val currentHpError: MutableState<Boolean>

    fun onSavePressed()
    fun onCancelPressed()
}
