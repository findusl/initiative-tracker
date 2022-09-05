package de.lehrbaum.initiativetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
		holder.bind(combatant)
	}

	inner class ViewHolder(val binding: FragmentInitiativeItemBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.root.setOnClickListener(this::onClick)
			binding.saveButton.setOnClickListener(this::onSave)
			binding.cancelButton.setOnClickListener(this::onCancel)
			binding.nameEdit.setSimpleItems(R.array.creatures)
		}

		fun bind(viewModel: CombatantViewModel) {
			binding.viewModel = viewModel
			// The binding sets the visibility values delayed, which leads to wrong measurements and weird effects
			// This way we set the visibility values immediately and the layout calculates measurements correctly.
			if (viewModel.editMode) {
				binding.standardView.visibility = View.GONE
				binding.editView.visibility = View.VISIBLE
			} else {
				binding.standardView.visibility = View.VISIBLE
				binding.editView.visibility = View.GONE
			}
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
			closeSoftKeyboard()
		}

		private fun validateInput(): Boolean {
			var valid = true
			val errorMessage = "This field cannot be blank"
			if (binding.initiativeEdit.text.toString().isBlank()) {
				binding.initiativeEditLayout.error = errorMessage
				valid = false
			} else {
				binding.initiativeEditLayout.error = null
			}
			if (binding.nameEdit.text.toString().isBlank()) {
				binding.nameEditLayout.error = errorMessage
				valid = false
			} else {
				binding.nameEditLayout.error = null
			}
			return valid
		}

		private fun onCancel() {
			viewModel.selectCombatant(null)
			closeSoftKeyboard()
		}

		private fun closeSoftKeyboard() {
			with(binding.root.context) {
				val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
				imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
			}
		}
	}
}

private class CombatantDiffUtil : DiffUtil.ItemCallback<CombatantViewModel>() {

	override fun areItemsTheSame(oldItem: CombatantViewModel, newItem: CombatantViewModel): Boolean {
		return oldItem.id == newItem.id
	}

	override fun areContentsTheSame(oldItem: CombatantViewModel, newItem: CombatantViewModel) = oldItem == newItem

}
