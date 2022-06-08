package de.lehrbaum.initiativetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.lehrbaum.initiativetracker.databinding.FragmentInitiativeItemBinding
import java.lang.Short.parseShort

class InitiativeRecyclerViewAdapter(private val viewModel: InitiativeViewModel) :
	ListAdapter<CombatantViewModel, InitiativeRecyclerViewAdapter.ViewHolder>(CombatantDiffUtil()) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		return ViewHolder(FragmentInitiativeItemBinding.inflate(inflater, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val combatant = getItem(position)
		holder.binding.viewModel = combatant
	}

	inner class ViewHolder(val binding: FragmentInitiativeItemBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.root.setOnClickListener(this::onClick)
			binding.saveButton.setOnClickListener(this::onSave)
			binding.cancelButton.setOnClickListener(this::onCancel)
		}

		private fun onClick() {
			binding.viewModel?.let { viewModel.selectCombatant(it) }
		}

		private fun onSave() {
			if (!validateInput()) return

			val updatedCombatant = binding.viewModel?.copy(
				name = binding.nameEdit.text.toString(),
				initiative = parseShort(binding.initiativeEdit.text.toString())
			)
			updatedCombatant?.let { viewModel.updateCombatant(it) }
			viewModel.selectCombatant(null)
		}

		private fun validateInput(): Boolean {
			var valid = true
			val errorMessage = "This field cannot be blank"
			if (binding.initiativeEdit.text.toString().isBlank()) {
				binding.initiativeEdit.error = errorMessage
				valid = false
			}
			if (binding.nameEdit.text.toString().isBlank()) {
				binding.nameEdit.error = errorMessage
				valid = false
			}
			return valid
		}

		private fun onCancel() {
			viewModel.selectCombatant(null)
		}
	}
}

private class CombatantDiffUtil : DiffUtil.ItemCallback<CombatantViewModel>() {

	override fun areItemsTheSame(oldItem: CombatantViewModel, newItem: CombatantViewModel): Boolean {
		return oldItem.id == newItem.id
	}

	override fun areContentsTheSame(oldItem: CombatantViewModel, newItem: CombatantViewModel) = oldItem == newItem

}
