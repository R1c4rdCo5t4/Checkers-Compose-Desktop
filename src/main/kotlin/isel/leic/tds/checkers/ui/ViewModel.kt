package isel.leic.tds.checkers.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.storage.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


class ViewModel(private val scope: CoroutineScope) {

    private val collectionName = "Checkers"
    private val connectStr = System.getenv("CONN_STRING")
    private val dbName = "checkers-console-ui"

    private val storage = MongoStorage(connectStr, dbName, collectionName, BoardSerializer)
    private val autoRefreshDelay = 3.seconds

    val refreshEnable: Boolean
        get() {
            val g = game ?: return false
            return !onRefresh && g.board is BoardRun && g.player != g.board.turn
        }

    val takebackEnable: Boolean
        get() = game?.board is BoardRun && game?.board?.lastMove != null

    var game: Game? by mutableStateOf(null)
        private set

    var openNewGameDialog by mutableStateOf(false)
        private set

    var openTakebackDialog by mutableStateOf(false)
        private set

    private var onRefresh by mutableStateOf(false)

    var autoRefresh by mutableStateOf(true)
        private set

    var clickedSquare : Square? by mutableStateOf(null)
        private set

    var showTargets by mutableStateOf(true)
        private set


    fun newGame(name: String? = null) {
        if (name!=null) {
            scope.launch {
                game = createGame(name, storage)
                if (autoRefresh) autoRefresh()
            }
        }
        toggleNewGameDialog()
    }

    fun play(dest: Square) {
        val g = checkNotNull(game)
        val clicked = clickedSquare

        clickedSquare = if(
            clicked != null &&
            g.board is BoardRun &&
            isPlayerTurn() &&
            dest in g.board.getPossibleSquares(clicked)
        ) {
            game = g.play(clicked, dest, storage, scope)
            null
        } else dest
    }

    fun refresh() {
        val g = game ?: return
        if (onRefresh) return
        onRefresh = true
        scope.launch {
            val board = storage.read(g.id)
            checkNotNull(board)
            game = g.copy(board = board)
            onRefresh = false

            if (board is BoardRun && board.requestingTakeback == g.player.other()){
                openTakebackDialog = true
            }
        }
    }

    private suspend fun autoRefresh() {
        while (true) {
            delay(autoRefreshDelay)
            refresh()
        }
    }

    private fun isPlayerTurn(): Boolean {
        val g = game ?: return false
        return g.board is BoardRun && g.player == g.board.turn
    }

    fun requestTakeback(){
        val g = game
        val b = g?.board
        if (g == null || b !is BoardRun) return

        val board = BoardRun(b.entries, b.turn, b.lastMove, g.player)
        scope.launch {
            storage.update(g.id, board)
        }
    }

    private fun takebackMove() {
        val prevGame = game ?: return
        val srcSquare = prevGame.board.lastMove?.src
        val destSquare = prevGame.board.lastMove?.dest
        val captured = prevGame.board.lastMove?.captured
        val entries = prevGame.board.entries.toMutableMap()
        val destPiece = entries[destSquare]

        if (srcSquare == null || destPiece == null) return
        entries.remove(destSquare)
        entries[srcSquare] = destPiece

        if (captured != null) {
            entries[captured.first] = captured.second
        }

        val prevBoard = prevGame.board
        if (prevBoard is BoardRun) {
            val b = BoardRun(entries.toMap(), prevBoard.turn.other(), null, null)
            game = Game(prevGame.id, prevGame.player, b)

            scope.launch {
                storage.update(prevGame.id, b)
            }
        }
    }


    fun acceptTakeback(){
        takebackMove()
        toggleTakebackDialog()
    }

    fun rejectTakeBack(){
        val g = game ?: return
        val b = g.board
        if(b !is BoardRun) return

        val board = BoardRun(b.entries, b.turn, null, null)
        game = Game(g.id, g.player, board)

        scope.launch {
            storage.update(g.id, board)
        }
        toggleTakebackDialog()
    }

    suspend fun exitGame(){
        val g = game
        if (g != null) { // exiting the game causes the opponent to win game (forfeit)
            val board = BoardWin(g.board.entries, g.player.other())
            storage.update(g.id, board)
        }
    }


    fun changeTheme(newTheme : String) {
        CustomTheme.currentTheme = CustomTheme.valueOf(newTheme.replace(" ", "")).getMaterialThemeColors()
    }

    fun toggleNewGameDialog(){
        openNewGameDialog = !openNewGameDialog
    }

    private fun toggleTakebackDialog(){
        openTakebackDialog = !openTakebackDialog
    }

    fun toggleShowTargets() {
        showTargets = !showTargets
    }

    fun toggleAutoRefresh(){
        autoRefresh = !autoRefresh
    }
}