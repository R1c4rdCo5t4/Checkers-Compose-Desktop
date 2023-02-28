package isel.leic.tds.checkers.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import isel.leic.tds.checkers.model.*

@Composable
fun RowsBarView(rowModifier: Modifier, columnModifier: Modifier) {

    Row(rowModifier, horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        repeat(BOARD_DIM){
            Column(columnModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center)
            {
                val txt = ('a' + it) // if(player == Player.B) ('a' + BOARD_DIM - it - 1) else ('a' + it)
                Text(txt.toString() , color = Color.White)
            }
        }
    }
}


@Composable
fun ColumnsBarView(rowModifier: Modifier, columnModifier: Modifier, player: Player?){

    Column(columnModifier, verticalArrangement = Arrangement.Center){
        repeat(BOARD_DIM){
            Row(rowModifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
            {
                val txt = if(player == Player.B) it + 1 else BOARD_DIM - it
                Text(txt.toString(), color = Color.White)
            }
        }
    }
}
