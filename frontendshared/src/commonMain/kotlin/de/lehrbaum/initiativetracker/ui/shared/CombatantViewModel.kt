package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ImageBitmap
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.main.MainViewModel

@Stable
data class CombatantViewModel(
	val ownerId: Long,
	val id: Long,
	val name: String,
	val creatureType: String?,
	val initiative: Int?,
	val maxHp: Int?,
	val currentHp: Int?,
	val disabled: Boolean,
	val isHidden: Boolean,
	val active: Boolean = false,
	val isOwned: Boolean = false,
) {

	val initiativeString: String = initiative?.toString() ?: "-"

	val healthPercentage: Double? = if (currentHp != null && maxHp != null) currentHp / maxHp.toDouble() else null

	/* This is a derived state since the monsters could still be loading. */
	val monsterDTO by derivedStateOf { creatureType?.let { MainViewModel.Cache.getMonsterByName(it) } }

	private var cachedImage: ImageBitmap? = null

	suspend fun loadImage(): ImageBitmap? {
		val monster = monsterDTO ?: return null
		cachedImage?.let { return it }
		cachedImage = GlobalInstances.bestiaryNetworkClient.loadImage(monster)
		return cachedImage
	}
}

fun CombatantModel.toCombatantViewModel(appOwnerId: Long, active: Boolean = false): CombatantViewModel {
	return CombatantViewModel(this.ownerId, id, name, creatureType, initiative, maxHp, currentHp,
		disabled, isHidden, active, this.ownerId == appOwnerId)
}
