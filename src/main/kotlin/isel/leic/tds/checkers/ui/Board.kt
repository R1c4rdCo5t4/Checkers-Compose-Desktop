package isel.leic.tds.checkers.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import isel.leic.tds.checkers.model.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource



@Composable
fun BoardView(
    game: Game?,
    boardSize: Dp,
    squareSize: Dp,
    clickedSquare: Square?,
    targets : Boolean,
    onPlay : (Square) -> Unit
){
    Column(
        Modifier
            .height(boardSize)
            .fillMaxHeight(0.95f))
    {
        repeat(BOARD_DIM) { line ->
            Row {
                repeat(BOARD_DIM) { col ->
                    val square =
                        Square(if(game?.player == Player.B) BOARD_DIM-line-1 else line, col)

                    SquareView(square, Modifier.size(squareSize), game, targets, clickedSquare){ // onClick
                        onPlay(square)
                    }
                }
            }
        }
    }
}

@Composable
fun SquareView(
    square: Square,
    modifier: Modifier,
    game: Game?,
    targets: Boolean,
    clickedSquare:Square?,
    onClick: () -> Unit
){
    val board = game?.board
    val piece = board?.get(square)
    val color = if(square.black) MaterialTheme.colors.primary else MaterialTheme.colors.secondary

    Box(modifier
        .background(highlightSquareColor(board, square, MaterialTheme.colors.surface, clickedSquare)?:color)
        .clickable(onClick = onClick)){
        if(piece != null){
            Box(Modifier
                .fillMaxSize(0.85f)
                .clip(CircleShape)
                .background(if(piece.player == Player.W) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary)
                .align(Alignment.Center)){
                if(piece.isQueen){
                    Image(
                        painterResource("queen.png"),
                        modifier = Modifier.fillMaxSize(0.6f).align(Alignment.Center),
                        contentDescription = "Queen",
                        colorFilter = ColorFilter.tint(if(piece.player == Player.W) Color.Black else Color.White)
                    )
                }
            }
        }
        if(board is BoardRun && clickedSquare != null && board.turn == game.player){
            val possibleSquares = board.getPossibleSquares(clickedSquare)
            if(square in possibleSquares && targets){
                Box(Modifier
                    .fillMaxSize(0.40f)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.Center))
            }
        }
    }
}

fun highlightSquareColor(board:Board?, square: Square, highlightColor: Color, clickedSquare: Square?): Color?{
    if(board != null){
        val src = board.lastMove?.src
        val dest = board.lastMove?.dest
        return if(square == src || square == dest || square == clickedSquare) highlightColor else null
    }
    return null
}