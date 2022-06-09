package de.lehrbaum.initiativetracker

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
class InitiativeFragment : Fragment() {

	private val viewModel by viewModels<InitiativeViewModel>()

	private lateinit var initiativeRecyclerViewAdapter: InitiativeRecyclerViewAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val binding = FragmentInitiativeBinding.inflate(inflater, container, false)
		binding.viewModel = viewModel

		initiativeRecyclerViewAdapter = InitiativeRecyclerViewAdapter(viewModel)
		binding.list.adapter = initiativeRecyclerViewAdapter

		viewModel.combatants.observe(viewLifecycleOwner) {
			initiativeRecyclerViewAdapter.submitList(it)
		}

		return binding.root
	}


}
