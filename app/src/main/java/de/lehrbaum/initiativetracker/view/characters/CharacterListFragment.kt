package de.lehrbaum.initiativetracker.view.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.lehrbaum.initiativetracker.bl.model.CharacterModel

class CharacterListFragment : Fragment(), CharacterListViewModelImpl.Delegate {

	private val characterListViewModel by viewModels<CharacterListViewModelImpl>()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		characterListViewModel.setDelegate(this, viewLifecycleOwner)
		return ComposeView(requireContext()).apply {
			setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
			setContent {
				MaterialTheme {
					CharacterListScreen(characterListViewModel)
				}
			}
		}
	}

	override fun editCharacter(characterModel: CharacterModel) {
		val action =
			CharacterListFragmentDirections.actionCharacterListFragmentToEditCharacterFragment(
				characterModel.id
			)
		findNavController().navigate(action)
	}
}
