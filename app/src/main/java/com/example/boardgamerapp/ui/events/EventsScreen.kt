package com.example.boardgamerapp.ui.events

import GameCard
import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
            text = "Kein Spiel gefunden.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
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

