package de.lehrbaum.initiativetracker.view.combat.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.lehrbaum.initiativetracker.databinding.ListItemCombatantBinding
import de.lehrbaum.initiativetracker.view.combat.CombatantViewModel

@Suppress("unused")
private const val TAG = "CombatClientRecyclerViewAdapter"

class CombatClientRecyclerViewAdapter(
	private val viewLifecycleOwner: LifecycleOwner
) :
	ListAdapter<CombatantViewModel, CombatClientRecyclerViewAdapter.ViewHolder>(CombatantDiffUtil()) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		return ViewHolder(ListItemCombatantBinding.inflate(inflater, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val combatant = getItem(position)
		holder.bind(combatant)
	}

	inner class ViewHolder(private val binding: ListItemCombatantBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.lifecycleOwner = viewLifecycleOwner
		}

		fun bind(combatantViewModel: CombatantViewModel) {
			binding.viewModel = combatantViewModel
		}
	}
}

private class CombatantDiffUtil : DiffUtil.ItemCallback<CombatantViewModel>() {

	override fun areItemsTheSame(oldItem: CombatantViewModel, newItem: CombatantViewModel): Boolean {
		return oldItem.id == newItem.id
	}

	override fun areContentsTheSame(oldItem: CombatantViewModel, newItem: CombatantViewModel) = oldItem == newItem

}
