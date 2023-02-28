package isel.leic.tds.checkers.model


const val BOARD_DIM = 8 // (1..('z'-'a') step 2)

enum class Player { W, B }

fun Player.other() = if (this == Player.W) Player.B else Player.W

fun Player?.toString() = this?.toString() ?: ""


data class Piece(val player: Player, val isQueen: Boolean)

fun Piece?.toString() = when (this?.isQueen) {
    null -> ""
    true -> player.name
    false -> player.name.lowercase()
}

fun Piece.checkPromotion(square: Square) = isQueen ||
        (player == Player.W && square.row.index == 0) ||
        (player == Player.B && square.row.index == BOARD_DIM - 1)


typealias Entry = Pair<Square, Piece>
typealias Entries = Map<Square, Piece>

fun Entries.removePiece(sq: Square) = this - sq
fun Entries.addPiece(entry: Entry) = this + entry
fun Entries.removeCapturedPiece(sq: Square?) = if (sq != null) this - sq else this


data class Move(val src: Square, val dest: Square, val captured: Entry? = null)

fun Move?.toString() = if (this != null) "$src,$dest,${captured?.first?:""},${captured?.second.toString()}" else ""

sealed class Board(val entries: Entries, val lastMove: Move?)
class BoardRun(pcs: Entries, val turn: Player, lastMove: Move? = null, val requestingTakeback: Player?=null) : Board(pcs, lastMove)
class BoardWin(pcs: Entries, val winner: Player, lastMove: Move? = null) : Board(pcs, lastMove)

fun initialBoard() = BoardRun(Square.values
    .filter { it.black && it.row.index !in BOARD_DIM / 2 - 1..BOARD_DIM / 2 }
    .associateWith { Piece(if (it.row.index < BOARD_DIM / 2 - 1) Player.B else Player.W, false) },
    Player.W
)

operator fun Board.get(square: Square) = entries[square]


inline fun Board.compareWith(board: Board, condition: (Map.Entry<Square, Piece>) -> Boolean) =
    entries.filter(condition) == board.entries.filter(condition)

fun Board.play(srcSquare: Square, destSquare: Square): Board = when (this) {
    is BoardWin -> error("Game is already over")
    is BoardRun -> {
        val piece = this[srcSquare]

        require(srcSquare.black && piece != null) { "Illegal origin: $srcSquare" }
        require(destSquare.black && this[destSquare] == null) { "Illegal destination: $destSquare" }
        require(piece.player == turn) { "The piece in $srcSquare is not yours" }

        val move = getMove(srcSquare, destSquare, piece)
        require(move != null) { "Illegal Move: $srcSquare -> $destSquare" }

        val newEntries = entries
            .removePiece(move.src)
            .addPiece(Entry(move.dest, Piece(turn, piece.checkPromotion(move.dest))))
            .removeCapturedPiece(move.captured?.first)

        // change turn unless turn player can take again after capture (chain capture)
        val nextTurn =
            if (move.captured?.first != null && // has captured
                BoardRun(newEntries, turn).getPossibleCaptures().any { it.src == move.dest }) // same piece still has captures
                turn
            else turn.other()

        if (checkWin(newEntries)) BoardWin(newEntries, turn, move) else BoardRun(newEntries, nextTurn, move) //nextTurn

    }
}

private fun BoardRun.checkWin(entries: Entries) = entries.values.none { it.player == turn.other() }

private fun BoardRun.getMove(srcSquare: Square, destSquare: Square, piece: Piece): Move? {
    val captures = getPossibleCaptures()
    return if (captures.isNotEmpty()) {
        val move = captures.firstOrNull { it.src == srcSquare && it.dest == destSquare }
        require(move != null) {
            "There ${if (captures.size > 1) "are mandatory captures"
            else "is a mandatory capture"} in: ${captures.toString().trim('[', ']')}"
        }
        move
    } else Move(srcSquare, destSquare).takeIf { it in getPossibleMovesFrom(srcSquare, piece) }
}

fun BoardRun.getPossibleCaptures(): List<Move> =
    entries.filterValues { it.player == turn }.map { (square, piece) ->
        if (piece.isQueen) getQueenCapturesFrom(square) else getCapturesFrom(square)
    }.flatten()


private fun BoardRun.getQueenCapturesFrom(square: Square): List<Move> {
    return square.getAllDiagonals().mapNotNull { diag ->
        val squareIndex = diag.indexOfFirst { entries[it] != null } // index of the first square blocking diagonal
        if (squareIndex != -1 && squareIndex != diag.lastIndex) { // check if square exists and is not the last
            if (entries[diag[squareIndex]]?.player == turn.other()) { // square must have opponent piece
                val diagAfterSquare = diag.slice(squareIndex + 1..diag.lastIndex)
                val indexOfNext = diagAfterSquare.indexOfFirst { entries[it] != null }
                val indexOfLast = if (indexOfNext != -1) indexOfNext+squareIndex else diag.lastIndex
                diag
                    .slice(squareIndex + 1..indexOfLast)
                    .map {
                        val capturedPiece = entries[diag[squareIndex]]
                        Move(square, it, if(capturedPiece != null ) Entry(diag[squareIndex], capturedPiece) else null)
                    }

            } else null
        } else null
    }.flatten()
}

private fun BoardRun.getCapturesFrom(sq: Square): List<Move> =
    listOf(1, -1).map { stepSign ->
        listOf(sq::slash, sq::backSlash).mapNotNull { slashType ->
            val capturedSquare = sq.moveDiagonal(turn, slashType, stepSign)
            val capturedPiece = entries[capturedSquare]

            if(capturedPiece != null && capturedSquare != null){
                sq.moveDiagonal(turn, slashType, stepSign * 2)
                    ?.let { to -> Move(sq, to, Entry(capturedSquare, capturedPiece)) }
            }
            else null
        }
    }.flatten()
        .filter { entries[it.captured?.first]?.player == turn.other() && it.dest !in entries.keys }


fun BoardRun.getPossibleMovesFrom(square: Square, piece: Piece): List<Move> {

    val possibleSquares =
        if (piece.isQueen) square.getAllDiagonals().map { diag ->
            diag.dropLast(diag.size - if (diag.none { it in entries.keys }) diag.size
            else diag.indexOfFirst { it in entries.keys })
        }.flatten()

        else listOfNotNull(
            square.moveDiagonal(piece.player, square::slash),
            square.moveDiagonal(piece.player, square::backSlash)
        ).filter { it !in entries.keys }

    // println("Possible moves from $square: $possibleSquares")
    return possibleSquares.map { Move(square, it) }
}

fun BoardRun.getPossibleSquares(src: Square) : List<Square>{
    val possibleCaptures = getPossibleCaptures()
    val srcPiece = this[src] ?: return emptyList()
    val possibleMoves = getPossibleMovesFrom(src, srcPiece)
    return possibleCaptures
        .ifEmpty { possibleMoves }
        .filter{it.src == src && this[src]?.player == turn}
        .map{it.dest}
}