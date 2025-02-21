package com.example.boardgamerapp.ui.games

import GameCard
import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.ui.events.getOrCreateUserId
import com.example.boardgamerapp.ui.main.PageTitle
import com.example.boardgamerapp.viewmodel.GameViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.ParseException
import java.util.Locale


@Composable
fun GamesScreen(
    gameViewModel: GameViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as Application
        )
    )
) {

    var gameList by remember { mutableStateOf<List<Game>?>(emptyList()) }

    LaunchedEffect(Unit) {
        gameViewModel.allUpcomingGames.collectLatest { games ->
            gameList = games
            println("Geladenes Spiele: $games")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PageTitle("bevorstehende Spieltermine")

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (gameList.isNullOrEmpty()) {
                Text(
                    text = "Keine Spiele gefunden.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                gameList?.let { list ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(list) { game ->
                            GameCard(game)
                        }
                    }
                }
            }
        }

        AddGameButton(gameViewModel)
    }
}

@Composable
fun AddGameButton(gameViewModel: GameViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Spieltermin hinzufügen")
    }

    if (showDialog) {
        AddGameDialog(
            onDismiss = { showDialog = false },
            onSave = { game ->
                gameViewModel.addGame(game)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddGameDialog(onDismiss: () -> Unit, onSave: (Game) -> Unit) {
    var gameName by remember { mutableStateOf("") }
    var gameDate by remember { mutableStateOf("") }
    var gameTime by remember { mutableStateOf("") }
    var gameLocation by remember { mutableStateOf("") }
    var newSuggestion by remember { mutableStateOf("") }
    var suggestedGames by remember { mutableStateOf(listOf<String>()) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Neues Spiel hinzufügen") },
        text = {
            Column {
                OutlinedTextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    label = { Text("Spielname") }
                )
                OutlinedTextField(
                    value = gameDate,
                    onValueChange = { gameDate = it },
                    label = { Text("Datum (TT.MM.JJJJ)") }
                )
                OutlinedTextField(
                    value = gameTime,
                    onValueChange = { gameTime = it },
                    label = { Text("Uhrzeit (HH:MM)") }
                )
                OutlinedTextField(
                    value = gameLocation,
                    onValueChange = { gameLocation = it },
                    label = { Text("Ort") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Spielvorschläge", style = MaterialTheme.typography.titleMedium)
                suggestedGames.forEach { suggestion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = { isChecked ->
                                suggestedGames =
                                    if (isChecked) suggestedGames + suggestion else suggestedGames - suggestion
                            }
                        )
                        Text(suggestion, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                // 🆕 **Neues Spiel vorschlagen**
                OutlinedTextField(
                    value = newSuggestion,
                    onValueChange = { newSuggestion = it },
                    label = { Text("Neues Spiel vorschlagen") }
                )
                Button(
                    onClick = {
                        if (newSuggestion.isNotBlank()) {
                            suggestedGames = suggestedGames + newSuggestion
                            newSuggestion = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Hinzufügen")
                }
            }
        },
        confirmButton = {
            val userId = LocalContext.current
            Button(
                onClick = {
                    if (gameName.isNotBlank() && gameDate.isNotBlank() && gameTime.isNotBlank() && gameLocation.isNotBlank()) {
                        onSave(
                            Game(
                                0,
                                gameName,
                                formatDateForDB(gameDate),
                                gameTime,
                                gameLocation,
                                suggestedGames,
                                getOrCreateUserId(userId)
                            )
                        )
                    }
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Abbrechen")
            }
        }
    )
}

fun formatDateForDB(inputDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        outputFormat.format(date!!) // Konvertiert zu `yyyy-MM-dd`
    } catch (e: ParseException) {
        inputDate // Falls die Eingabe falsch ist, bleibt sie unverändert
    }
}

