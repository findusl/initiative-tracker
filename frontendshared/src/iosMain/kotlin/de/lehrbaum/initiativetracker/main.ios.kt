import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import de.lehrbaum.initiativetracker.ui.main.MainComposable
import de.lehrbaum.initiativetracker.ui.main.MainViewModel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController {
	val mainViewModel = remember { MainViewModel() }
	Napier.base(DebugAntilog())
	MainComposable(mainViewModel)
}