import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import de.lehrbaum.initiativetracker.ui.main.MainScreen
import de.lehrbaum.initiativetracker.ui.main.MainViewModelImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController {
	val mainViewModel = remember { MainViewModelImpl() }
	Napier.base(DebugAntilog())
	MaterialTheme {
		MainScreen(mainViewModel)
	}
}