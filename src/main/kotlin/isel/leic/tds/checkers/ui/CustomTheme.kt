package isel.leic.tds.checkers.ui

import androidx.compose.runtime.*
import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import java.lang.Float.min


enum class CustomTheme(
    private val blackSquareColor: Color,
    private val whiteSquareColor: Color,
    private val background: Color,
    private val blackPieceColor: Color = Color.Black,
    private val whitePieceColor: Color = Color.White,
){
    Classic(Color(112,80,59), Color(207,175,139), Color(53,42,38), Color(59,52,49), Color(229,213,193)),
    Gray(Color(120,120,120), Color(220,220,220),  Color(39,36,33), Color(50,50,50)),
    Blue(Color(124,144,190), Color(240,240,240), Color(78,92,120)),
    Red(Color(233,83,67), Color(220,220,220), Color(138,49,40)),
    Orange(Color(252,145,99), Color(242,226,210), Color(158,88,59)),
    Green(Color(118,150,86), Color(238,238,210), Color(66,84,48));


    companion object {
        var currentTheme : Colors by mutableStateOf(Classic.getMaterialThemeColors())
    }

    // dynamically gets the highlighted move color
    private fun highlightSquareColor(): Color{
        val r = min(1f, blackSquareColor.red + 0.2f)
        val g = min(1f, blackSquareColor.green + 0.2f)
        val b = min(1f, blackSquareColor.blue + 0.2f)
        return blackSquareColor.copy(red=r, green=g, blue=b)
    }

    fun getMaterialThemeColors(): Colors{
        return Colors(
            blackSquareColor, blackSquareColor, whiteSquareColor, whiteSquareColor, background, highlightSquareColor(),
            whiteSquareColor, whitePieceColor, blackPieceColor, background, blackSquareColor, blackSquareColor, false
        )
    }
}


