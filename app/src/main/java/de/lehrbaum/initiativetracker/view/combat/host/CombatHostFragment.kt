package de.lehrbaum.initiativetracker.view.combat.host

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.lehrbaum.initiativetracker.R
import de.lehrbaum.initiativetracker.databinding.FragmentCombatHostBinding


/**
 * A fragment representing a list of Items.
 */
class CombatHostFragment : Fragment(), CombatHostViewModel.Delegate, MenuProvider {

	private val viewModel by viewModels<CombatHostViewModel>()

	private lateinit var combatHostRecyclerViewAdapter: CombatHostRecyclerViewAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewModel.setDelegate(this, viewLifecycleOwner)

		val binding = FragmentCombatHostBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = viewLifecycleOwner
		binding.viewModel = viewModel

		combatHostRecyclerViewAdapter = CombatHostRecyclerViewAdapter(viewModel, viewLifecycleOwner)
		binding.list.adapter = combatHostRecyclerViewAdapter

		viewModel.combatants.observe(viewLifecycleOwner) {
			combatHostRecyclerViewAdapter.submitList(it)
		}

		ItemTouchHelper(ItemTouchCallback()).attachToRecyclerView(binding.list)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		requireActivity().addMenuProvider(this, viewLifecycleOwner)
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

	override fun showSessionId(sessionCode: Int) {
		view?.let {
			Snackbar
				.make(it, "Session code is $sessionCode", Snackbar.LENGTH_LONG)
				.setAction("Copy") {
					val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
					val clip = ClipData.newPlainText("Combat Session Id", sessionCode.toString())
					clipboard!!.setPrimaryClip(clip)
				}
				.show()
		}
	}

	private inner class ItemTouchCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
		override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

		override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
			viewModel.deleteCombatant(viewHolder.absoluteAdapterPosition)
			view?.let {
				Snackbar
					.make(it, "Deleted combatant", Snackbar.LENGTH_LONG)
					.setAction("Undo") { viewModel.undoDelete() }
					.show()
			}
		}
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_combat_host, menu)
	}

	override fun onPrepareMenu(menu: Menu) {
		val shareItem = menu.findItem(R.id.action_share)
		shareItem.isVisible = !viewModel.isSharing
		val stopShareItem = menu.findItem(R.id.action_stop_sharing)
		stopShareItem.isVisible = viewModel.isSharing
		val showSessionIdItem = menu.findItem(R.id.action_show_session_id)
		showSessionIdItem.isVisible = viewModel.isSharing
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		return when (menuItem.itemId) {
			R.id.action_share -> {
				viewModel.onShareClicked()
				true
			}
			R.id.action_stop_sharing -> {
				viewModel.onStopShareClicked()
				true
			}
			R.id.action_show_session_id -> {
				viewModel.showSessionId()
				true
			}
			else -> false
		}
	}

}
