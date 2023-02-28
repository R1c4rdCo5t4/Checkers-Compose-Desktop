package isel.leic.tds.checkers.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import isel.leic.tds.checkers.model.*


@Composable
fun StatusBarView(mod: Modifier,g: Game?){
    val board = g?.board
    val (txt,turn) = when(board) {
        is BoardRun -> "Turn:" to board.turn
        is BoardWin -> "Winner:" to board.winner
        null -> "Start a new game" to null
    }
    val player = if(g == null) "" else "Player: ${g.player}"

    Row(mod.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusBarText("$txt ${turn ?: ""}")
        if(g != null){
            StatusBarText(player)
            StatusBarText("ID: ${g.id}")
        }
    }
}

@Composable
fun StatusBarText(text: String){
    val style: TextStyle = MaterialTheme.typography.h5
    Text(text, color = Color.White, style = style)
}