package isel.leic.tds.checkers.model

class Row private constructor(val index: Int) {
    val number get() = index.getRowIndexOrNumber()

    companion object {
        val values = (0 until BOARD_DIM).map { Row(it) }
        operator fun invoke(number: Int) = values[number.getRowIndexOrNumber()]
    }

    override fun toString() = "$number"
}


fun Int.getRowIndexOrNumber() = BOARD_DIM - this

fun String.getRowNumber() = when {
    this.slice(0..1).all { it.isDigit() } -> this.slice(0..1).toInt()
    this.first().isDigit() -> this.first().digitToInt()
    else -> null
}

fun Int.toRowOrNull() = if (this in (BOARD_DIM downTo 1)) Row(this) else null

fun Int.indexToRow() = Row(getRowIndexOrNumber())


