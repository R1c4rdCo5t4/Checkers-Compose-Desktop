package isel.leic.tds.checkers.model

class Square private constructor(val row: Row, val column: Column) {

    val black get() = (row.index + column.index) % 2 != 0

    init {
        require(BOARD_DIM % 2 == 0) { "Board dimension should be an even number" }
    }

    companion object {
        val values = Row.values.map { r -> Column.values.map { c -> Square(r, c) } }.flatten()

        operator fun invoke(rowIdx: Int, columnIdx: Int) =
            values.first { rowIdx == it.row.index && columnIdx == it.column.index }

        operator fun invoke(row: Row, column: Column) = values.first { row == it.row && column == it.column }

    }

    override fun toString() = "$row$column"
}

fun String.toSquareOrNull(): Square? {
    return if (this.length > 3 || this.count { it.isLetter() } != 1) null
    else Square(getRowNumber()?.toRowOrNull() ?: return null, getColumnSymbol()?.toColumnOrNull() ?: return null)
}

fun Square.slash() = this slashWithSign 1
fun Square.backSlash() = this slashWithSign -1

infix fun Square.slashWithSign(sign: Int) =
    Square.values.filter { row.index + column.index * sign == it.row.index + it.column.index * sign }


inline fun Square.moveDiagonal(player: Player, diagType: () -> List<Square>, steps: Int = 1): Square? {
    val diag = diagType()
    val p = if (player == Player.W) 1 else -1
    val toIndex = diag.indexOf(this) - steps * p
    return if (toIndex in diag.indices) diag[toIndex] else null
}

fun Square.getAllDiagonals(): List<List<Square>> {
    val slash = slash()
    val backSlash = backSlash()
    val slashIndex = slash.indexOf(this)
    val backSlashIndex = backSlash.indexOf(this)

    // lists beginning from queen (exclusive) to corners diagonally
    val slashLeft = slash.slice(slashIndex..slash.lastIndex).drop(1)
    val slashRight = slash.slice(0 until slashIndex).reversed()
    val backSlashRight = backSlash.slice(backSlashIndex..backSlash.lastIndex).drop(1)
    val backSlashLeft = backSlash.slice(0 until backSlashIndex).reversed()

    return listOf(backSlashLeft, backSlashRight, slashLeft, slashRight)
}
