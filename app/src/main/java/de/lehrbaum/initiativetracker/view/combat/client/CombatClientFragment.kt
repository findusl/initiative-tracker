package de.lehrbaum.initiativetracker.view.combat.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import de.lehrbaum.initiativetracker.databinding.FragmentCombatClientBinding
import de.lehrbaum.initiativetracker.extensions.viewModelsFactory

class CombatClientFragment : Fragment(), CombatClientViewModel.Delegate {

	private val args: CombatClientFragmentArgs by navArgs()

	private val viewModel by viewModelsFactory { CombatClientViewModel(args.sessionId) }

	private lateinit var combatClientRecyclerViewAdapter: CombatClientRecyclerViewAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewModel.setDelegate(this, viewLifecycleOwner)

		val binding = FragmentCombatClientBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = viewLifecycleOwner

		combatClientRecyclerViewAdapter = CombatClientRecyclerViewAdapter(viewLifecycleOwner)
		binding.list.adapter = combatClientRecyclerViewAdapter

		viewModel.combatants.observe(viewLifecycleOwner) {
			combatClientRecyclerViewAdapter.submitList(it)
		}

		return binding.root
	}

}