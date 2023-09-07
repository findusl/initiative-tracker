import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import de.lehrbaum.initiativetracker.ui.main.MainScreen
import de.lehrbaum.initiativetracker.ui.main.MainViewModelImpl
import de.lehrbaum.initiativetracker.ui.shared.ProvideScreenSizeInformation
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController {
	Napier.base(DebugAntilog())
	ProvideScreenSizeInformation(0, 0) {
		MaterialTheme {
			MainScreen(MainViewModelImpl())
		}
	}
}