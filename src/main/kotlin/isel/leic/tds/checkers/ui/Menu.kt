package isel.leic.tds.checkers.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar


@Composable
fun FrameWindowScope.Menu(viewModel: ViewModel, onExit: () -> Unit) {

    MenuBar {
        Menu("Game") {
            Item("New") { viewModel.newGame() }
            Item("Refresh",
                onClick = viewModel::refresh,
                enabled = viewModel.refreshEnable
            )
            Item("Exit", onClick = onExit)
        }

        Menu("Options"){
            CheckboxItem("Show Targets", checked = viewModel.showTargets, onCheckedChange = { viewModel.toggleShowTargets() })
            CheckboxItem("Auto-Refresh", checked = viewModel.autoRefresh, onCheckedChange = { viewModel.toggleAutoRefresh() })
            Item("Request Takeback", onClick = viewModel::requestTakeback, enabled = viewModel.takebackEnable)
            Menu("Themes"){
                CustomTheme.values().forEach {
                    Item(it.name){
                        viewModel.changeTheme(it.name)
                    }
                }
            }
        }
    }
}