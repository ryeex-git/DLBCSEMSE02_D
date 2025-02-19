package com.example.boardgamerapp.ui.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameVote
import com.example.boardgamerapp.viewmodel.GameViewModel


@Composable
fun GameVoteDialog(
    userId: String,
    gameViewModel: GameViewModel,
    nextGame: Game, // Direkt das nächste Spiel übergeben
    onDismiss: () -> Unit
) {
    val checkedStateMap = remember { mutableStateMapOf<String, Boolean>() }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Spieleabend: ${nextGame.name}") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Für welches Spiel willst du abstimmen?",
                    style = MaterialTheme.typography.bodyLarge
                )
                nextGame.suggestedGames.forEach { suggestion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        Checkbox(
                            checked = checkedStateMap[suggestion] ?: false,
                            onCheckedChange = { checked ->
                                checkedStateMap[suggestion] = checked
                            }
                        )
                        Text(text = suggestion, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                GameSuggestionsScreen(nextGame, gameViewModel)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedGames = checkedStateMap.filterValues { it }.keys.toList()
                    if (selectedGames.isNotEmpty()) {
                        gameViewModel.voteForGame(
                            GameVote(
                                gameId = nextGame.id,
                                userId = userId,
                                gameName = selectedGames
                            )
                        )
                    }
                    onDismiss()
                }
            ) {
                Text("Abstimmen")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Abbrechen")
            }
        }
    )
}

@Composable
fun GameSuggestionsScreen(game: Game, gameViewModel: GameViewModel) {
    var newSuggestion by remember { mutableStateOf("") }
    //        Neues Spiel vorschlagen
    OutlinedTextField(
        value = newSuggestion,
        onValueChange = { newSuggestion = it },
        label = { Text("Neues Spiel vorschlagen") },
        modifier = Modifier.fillMaxWidth()
    )

    Button(
        onClick = {
            val updatedList = (game.suggestedGames + newSuggestion).distinct()
            gameViewModel.updateGameSuggestions(game.id, updatedList)
            newSuggestion = ""
        },
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text("Spiel vorschlagen")
    }
}
