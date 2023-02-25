package de.lehrbaum.initiativetracker.view.combat.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import de.lehrbaum.initiativetracker.extensions.viewModelsFactory
import de.lehrbaum.initiativetracker.logic.CharacterRepository
import javax.inject.Inject

@AndroidEntryPoint
class EditCharacterFragment : Fragment() {

	private val args: EditCharacterFragmentArgs by navArgs()

	@Inject
	protected lateinit var characterRepository: CharacterRepository

	private val editCharacterViewModel by viewModelsFactory { EditCharacterViewModelImpl(args.characterId, characterRepository) }

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return ComposeView(requireContext()).apply {
			setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
			setContent {
				MaterialTheme {
					EditCharacterScreen(editCharacterViewModel = editCharacterViewModel)
				}
			}
		}
	}
}
