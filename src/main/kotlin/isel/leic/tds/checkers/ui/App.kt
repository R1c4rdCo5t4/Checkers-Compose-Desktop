package isel.leic.tds.checkers.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.FrameWindowScope
import isel.leic.tds.checkers.model.*
import kotlinx.coroutines.launch


@Preview
@Composable
fun FrameWindowScope.App(winSize: Dp, onExitApp: () -> Unit, exit: Boolean){
    val miniBarSize = 20.dp
    val boardSize = winSize / 1.25f
    val squareSize = boardSize / BOARD_DIM
    val scope = rememberCoroutineScope()
    val viewModel = remember{ ViewModel(scope) }
    val onExit = {
        scope.launch{
            viewModel.exitGame()
            onExitApp()
        }
        println("Bye.")
    }

    if (exit) onExit()

    Menu(viewModel, onExit)
    if (viewModel.openNewGameDialog){
        DialogNewGame(
            onOk = { name -> viewModel.newGame(name) },
            onCancel = { viewModel.toggleNewGameDialog() }
        )
    }

    if (viewModel.openTakebackDialog){
        DialogTakebackMove(
            onOk = viewModel::acceptTakeback,
            onCancel = viewModel::rejectTakeBack
        )
    }


    Row(Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center)
    {
        Column { // left col
            Row(Modifier.height(miniBarSize)){}

            ColumnsBarView(
                Modifier.height(squareSize).width(miniBarSize),
                Modifier.height(boardSize),
                viewModel.game?.player)

            Row(Modifier.height(miniBarSize*2)){}
        }

        Column(verticalArrangement = Arrangement.Center) { // middle col

                RowsBarView(
                    Modifier.width(boardSize),
                    Modifier.width(squareSize)
                )

                BoardView(
                    viewModel.game,
                    boardSize,
                    squareSize,
                    viewModel.clickedSquare,
                    viewModel.showTargets,
                    onPlay = viewModel::play
                )

                StatusBarView(Modifier.fillMaxWidth(0.85f).height(miniBarSize*2), viewModel.game)
        }
    }
}





