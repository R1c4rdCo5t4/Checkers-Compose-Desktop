package isel.leic.tds.checkers.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*


@Composable
fun DialogNewGame(onOk: (String) -> Unit, onCancel: () -> Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "New Game",
    state = DialogState(width = 300.dp, height = Dp.Unspecified)
) {
    var name by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(
            name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { if (name != "") onOk(name) else Unit }) { Text("New") }
            Button(onClick = onCancel) { Text("Cancel") }
        }
    }
}


@Composable
fun DialogTakebackMove(onOk: () -> Unit, onCancel: () -> Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "Opponent is asking for a takeback",
    state = DialogState(width = 300.dp, height = Dp.Unspecified)
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onOk) { Text("Accept") }
            Button(onClick = onCancel) { Text("Reject") }
        }
    }
}
