package de.lehrbaum.initiativetracker.view.combat.client

import android.os.Bundle
import android.view.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import de.lehrbaum.initiativetracker.R
import de.lehrbaum.initiativetracker.extensions.showSnackbar
import de.lehrbaum.initiativetracker.extensions.viewModelsFactory

class CombatClientFragment : Fragment(), CombatClientViewModelImpl.Delegate, MenuProvider {

	private val args: CombatClientFragmentArgs by navArgs()

	private val viewModel by viewModelsFactory { CombatClientViewModelImpl(args.sessionId) }

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewModel.setDelegate(this, viewLifecycleOwner)
		return ComposeView(requireContext()).apply {
			setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
			setContent {
				MaterialTheme {
					CombatClientScreen(viewModel)
				}
			}
		}
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
		showSnackbar("Combat ended", Snackbar.LENGTH_LONG)
		findNavController().popBackStack()
	}
}