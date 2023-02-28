
import isel.leic.tds.checkers.ui.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*


val winSize = 550.dp


fun main() = application {
    var exit by mutableStateOf(false)
    Window(
        onCloseRequest = { exit = true },
        resizable = false,
        title = "Checkers",
        icon = painterResource("icon.png"),
        state = WindowState(
            position= WindowPosition(Alignment.Center),
            size = DpSize(winSize, winSize)
        )
    ) {

        MaterialTheme(colors = CustomTheme.currentTheme) {
            App(winSize, ::exitApplication, exit)
        }
    }
}