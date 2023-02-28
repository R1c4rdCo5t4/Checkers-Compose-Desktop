package isel.leic.tds.checkers.model

class Column private constructor(val index: Int) {
    val symbol get() = index.getColumnSymbol()

    companion object{
        val values = (0 until BOARD_DIM).map{ Column(it) }

        operator fun invoke(symbol: Char) = values[symbol.getColumnIndex()]
        operator fun invoke(index:Int) = values[index]
    }

    override fun toString() = "$symbol"
}


fun Char.getColumnIndex() = this.code - 'a'.code

fun Int.getColumnSymbol() = ('a'.code + this).toChar()

fun String.getColumnSymbol() = if(this.last().isLetter()) this.last() else null


fun Char.toColumnOrNull() = if (getColumnIndex() in (0 until BOARD_DIM)) Column(getColumnIndex()) else null

fun Int.indexToColumn() = Column(this)


