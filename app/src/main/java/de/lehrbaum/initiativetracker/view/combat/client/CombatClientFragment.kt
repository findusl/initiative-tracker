package de.lehrbaum.initiativetracker.view.combat.client

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import de.lehrbaum.initiativetracker.R
import de.lehrbaum.initiativetracker.databinding.FragmentCombatClientBinding
import de.lehrbaum.initiativetracker.extensions.viewModelsFactory

class CombatClientFragment : Fragment(), CombatClientViewModel.Delegate, MenuProvider {

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

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		requireActivity().addMenuProvider(this, viewLifecycleOwner)
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_combat_client, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		return when (menuItem.itemId) {
			R.id.action_leave -> {
				leaveCombat()
				true
			}
			else -> false
		}
	}

	override fun leaveCombat() {
		parentFragmentManager.popBackStack()
	}
}