package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.storage.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


data class Game(
    val id: String,
    val player: Player,
    val board: Board
)

/**
 * Creates game in specified storage according to id
 * @param id id of the game
 * @param storage storage to store the game
 * @return game with initial board
 */
suspend fun createGame(id: String, storage: Storage<String, Board>): Game {
    val board = storage.read(id)

    if (board != null){
        val firstMove = board.compareWith(initialBoard()) { it.value.player == Player.B }
        if(board !is BoardWin && firstMove) return Game(id, Player.B, board) // keep already stored in storage
    }

    if (board != null) storage.delete(id) // remove existing board before creating a new one
    return Game(id, Player.W, initialBoard().also { storage.create(id, it) })
}

/**
 * Updates in storage and returns game with specified move according to <from> and <to>
 * @param from square with the piece to move
 * @param to square to move piece
 * @param storage storage to store the game
 * @return updated game
 */
fun Game.play(from: Square, to: Square, storage: Storage<String, Board>, scope: CoroutineScope): Game {
    check(board is BoardRun) { "Game is already over" }
    check(player == board.turn) { "Not your turn" }
    val newBoard = board.play(from, to)
    scope.launch {
        storage.update(this@play.id, newBoard)
    }
    return copy(board = newBoard)
}

