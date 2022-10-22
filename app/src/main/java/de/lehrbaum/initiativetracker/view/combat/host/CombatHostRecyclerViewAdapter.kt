package de.lehrbaum.initiativetracker.view.combat.host

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.lehrbaum.initiativetracker.databinding.ListItemCombatantBinding
import de.lehrbaum.initiativetracker.extensions.setOnClickListener
import de.lehrbaum.initiativetracker.view.combat.CombatantViewModel
import java.lang.Short.parseShort

@Suppress("unused")
private const val TAG = "CombatHostRecyclerViewAdapter"

class CombatHostRecyclerViewAdapter(
	private val viewModel: CombatHostViewModel,
	private val viewLifecycleOwner: LifecycleOwner
) :
	ListAdapter<CombatantViewModel, CombatHostRecyclerViewAdapter.ViewHolder>(CombatantDiffUtil()) {

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
			binding.root.setOnClickListener(this::onClick)
			binding.saveButton.setOnClickListener(this::onSave)
			binding.cancelButton.setOnClickListener(this::onCancel)
			viewModel.allMonsterNamesLiveData.observe(viewLifecycleOwner) {
				binding.nameEdit.setSimpleItems(it)
			}
			binding.nameEdit.doOnTextChanged { _, _, _, _ -> onChange() }
			binding.initiativeEdit.doOnTextChanged { _, _, _, _ -> onChange() }
		}

		fun bind(combatantViewModel: CombatantViewModel) {
			binding.viewModel = combatantViewModel
			// The binding sets the visibility values delayed, which leads to wrong measurements and weird effects
			// This way we set the visibility values immediately and the layout calculates measurements correctly.
			if (combatantViewModel.editMode) {
				binding.standardView.visibility = View.GONE
				binding.editView.visibility = View.VISIBLE
			} else {
				binding.standardView.visibility = View.VISIBLE
				binding.editView.visibility = View.GONE
			}
			if (combatantViewModel.editMode) {
				viewModel.currentlyEditingCombatant = combatantViewModel
			}
		}

		private fun onChange() {
			if (binding.viewModel?.editMode == true) {
				viewModel.currentlyEditingCombatant = calculateUpdatedCombatantViewModel()
			}
		}

		private fun onClick() {
			binding.viewModel?.let { viewModel.selectCombatant(it) }
		}

		private fun onSave() {
			if (!validateInput()) return

			// Let it throw should this be null
			calculateUpdatedCombatantViewModel()!!.let { viewModel.updateCombatant(it) }
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

		private fun calculateUpdatedCombatantViewModel(): CombatantViewModel? {
			return try {
				val initiative = parseShort(binding.initiativeEdit.text.toString())
				binding.viewModel?.copy(
					name = binding.nameEdit.text.toString(),
					initiative = initiative
				)
			} catch (e: java.lang.NumberFormatException) {
				// happens when the input is not a complete number, e.g. only minus or empty
				null
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