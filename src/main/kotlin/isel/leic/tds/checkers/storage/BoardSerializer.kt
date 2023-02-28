package isel.leic.tds.checkers.storage

import isel.leic.tds.checkers.model.*

object BoardSerializer : Serializer<Board, String> {
    override fun write(obj: Board): String {
        val boardType = obj::class.simpleName
        val turn = when (obj) {
            is BoardRun -> obj.turn
            is BoardWin -> obj.winner
        }
        val sep = System.lineSeparator()
        val takebackRequest = if(obj is BoardRun && obj.requestingTakeback != null) "${obj.requestingTakeback}?" else ""

        return boardType + sep + turn + sep +
                obj.entries
                    .map { it.key.toString() + if (it.value.isQueen) it.value.player else it.value.player.name.lowercase() } // <Square><Piece>
                    .joinToString(sep) + sep + obj.lastMove.toString() + sep + takebackRequest
    }


    override fun parse(stream: String): Board {

        val words = stream.split(System.lineSeparator())
        val boardType = words[0]
        val turn = words[1]

        val entries = words
            .drop(2)
            .dropLast(2)
            .filter { it.isNotEmpty() }
            .associate { str ->
                val squareStringDim = str.indexOfFirst { it.isLetter() } + 1
                require(str.length == squareStringDim + 1) {
                    "Each line must have exactly ${squareStringDim + 1} chars with <Square><Piece>"
                }
                val squareStr = str.substring(0 until squareStringDim)
                val square = squareStr.toSquareOrNull()
                requireNotNull(square) { "Invalid input string for square: $squareStr" }
                val piece = Piece(Player.valueOf(str[squareStringDim].uppercase()), str[squareStringDim].isUpperCase())
                Entry(square, piece)

            }

        val lastMoveLine = words[words.size-2].split(",")
        val lastMove : Move?

        if (lastMoveLine.size > 1){
            val lastSrc = lastMoveLine[0].toSquareOrNull()
            val lastDest = lastMoveLine[1].toSquareOrNull()
            require(lastSrc != null && lastDest != null) { "Invalid input string for square" }
            var lastCaptured : Entry? = null

            if (lastMoveLine.size > 2 && lastMoveLine[2] != "" && lastMoveLine[3] != ""){
                val capturedSquare = lastMoveLine[2].toSquareOrNull()
                val capturedPiecePlayer = Player.valueOf(lastMoveLine[3].uppercase())
                val capturedPiece = Piece(capturedPiecePlayer, lastMoveLine[3].first().isUpperCase())
                requireNotNull(capturedSquare) { "Captured square cannot be null" }
                lastCaptured = Entry(capturedSquare, capturedPiece)
            }
            lastMove = Move(lastSrc, lastDest, lastCaptured)
        }
        else {
            lastMove = null
        }

        val takebackRequest = if(words.last().length > 1) Player.valueOf(words.last().trim('?')) else null
        val player = Player.valueOf(turn)

        return when (boardType) {
            BoardRun::class.simpleName -> BoardRun(entries, player, lastMove, takebackRequest)
            BoardWin::class.simpleName -> BoardWin(entries, player, lastMove)
            else -> error("There is not that type of board for $boardType")
        }
    }
}