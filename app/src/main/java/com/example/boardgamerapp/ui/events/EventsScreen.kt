package com.example.boardgamerapp.ui.events

import GameCard
import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.boardgamerapp.R
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.ui.games.GameVoteDialog
import com.example.boardgamerapp.ui.main.PageTitle
import com.example.boardgamerapp.viewmodel.GameViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
    )
) {
    var nextGame by remember { mutableStateOf<Game?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogVote by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        gameViewModel.nextGame.collectLatest { game ->
            nextGame = game
            println("Geladenes Spiel: $game")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        PageTitle(subTitle = "Nächster Spieleabend")

        Spacer(modifier = Modifier.height(16.dp))


        nextGame?.let { game ->
            GameCard(game)

            val context = LocalContext.current
            val gameVotes by gameViewModel.getGameVotes(game.id)
                .collectAsState(initial = emptyList())
            val userId = getOrCreateUserId(context)

            if (!gameVotes?.any { it.userId == userId }!!) {
                Button(onClick = { showDialog = true }) {
                    Text("Spielabstimmung")
                }
            } else {
                Button(enabled = false, onClick = {}) {
                    Text("Bereits abgestimmt")
                }
                Text(
                    text = "Du hast gestimmt für: ${gameVotes?.find { it.userId == userId }!!.gameName.joinToString()}"
                )
            }


            if (showDialog && nextGame != null) {
                GameVoteDialog(
                    getOrCreateUserId(context),
                    gameViewModel,
                    game,
                    onDismiss = { showDialog = false })
            }
        } ?: Text(
            text = "Kein bevorstehendes Spiel gefunden.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )


        val pastGames = gameViewModel.allPastGames.collectAsState(initial = emptyList()).value
        if (pastGames!!.isNotEmpty()) {
            Button(onClick = { showDialogVote = true }) {
                Text("Vergangene Spielabende bewerten")
            }
        } else {
            Button(onClick = {}, enabled = false) {
                Text("Keine vergangenen Spiele")
            }
        }


        if (showDialogVote) {
            val context = LocalContext.current
            RatePastEventDialog(
                pastGames,
                onDismiss = { showDialogVote = false },
                onSubmitRating = { game, rating ->
                    gameViewModel.submitGameRating(game.id, getOrCreateUserId(context), rating)
                })
        }
    }
}

fun getOrCreateUserId(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userId = sharedPreferences.getString("user_id", null)

    if (userId == null) {
        userId = UUID.randomUUID().toString()
        sharedPreferences.edit().putString("user_id", userId).apply()
    }

    return userId
}

@Composable
fun EventsScreenWithPadding(innerPadding: PaddingValues) {
    EventsScreen(modifier = Modifier.padding(innerPadding))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatePastEventDialog(
    pastGames: List<Game>,
    onDismiss: () -> Unit,
    onSubmitRating: (Game, Float) -> Unit,
) {
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var rating by remember { mutableStateOf(0f) }
    var expanded by remember { mutableStateOf(false) }

    println(pastGames)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Vergangenes Event bewerten") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedGame?.name ?: "Event auswählen",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown öffnen"
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (pastGames.isNotEmpty()) {
                            pastGames.forEach { game ->
                                DropdownMenuItem(
                                    text = { Text(game.name) },
                                    onClick = {
                                        selectedGame = game
                                        expanded = false
                                    }
                                )
                            }
                        } else {
                            DropdownMenuItem(
                                text = { Text("Keine vergangenen Events") },
                                onClick = { expanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i * 2f }) {
                            if (rating >= i * 2f) {
                                // Voller Stern
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Stern",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(40.dp)
                                )
                            } else if (rating >= i * 2f - 1) {
                                // Halber Stern
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_star_half_24),
                                    contentDescription = "Halber Stern",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                // Leerer Stern
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_star_border_24),
                                    contentDescription = "Leerer Stern",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Bewertung: ${rating.toInt()} / 10",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedGame?.let { game ->
                        onSubmitRating(game, rating)
                        onDismiss()
                    }
                },
                enabled = selectedGame != null && rating > 0
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