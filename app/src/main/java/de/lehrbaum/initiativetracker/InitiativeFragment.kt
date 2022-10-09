package de.lehrbaum.initiativetracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.lehrbaum.initiativetracker.databinding.FragmentInitiativeBinding

/**
 * A fragment representing a list of Items.
 */
class InitiativeFragment : Fragment(), InitiativeViewModel.Delegate {

	private val viewModel by viewModels<InitiativeViewModel>()

	private lateinit var initiativeRecyclerViewAdapter: InitiativeRecyclerViewAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewModel.setDelegate(this, viewLifecycleOwner)

		val binding = FragmentInitiativeBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = viewLifecycleOwner
		binding.viewModel = viewModel

		initiativeRecyclerViewAdapter = InitiativeRecyclerViewAdapter(viewModel, viewLifecycleOwner)
		binding.list.adapter = initiativeRecyclerViewAdapter

		viewModel.combatants.observe(viewLifecycleOwner) {
			initiativeRecyclerViewAdapter.submitList(it)
		}

		return binding.root
	}

	override fun showSaveChangesDialog(onOkListener: () -> Unit) {
		AlertDialog.Builder(context)
			.setTitle("Save changes?")
			.setMessage("Do you want to save your changes?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.ok) { _, _ ->
				onOkListener()
			}
			.setNegativeButton(android.R.string.cancel, null)
			.show()
	}

}
