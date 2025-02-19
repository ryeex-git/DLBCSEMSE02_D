package com.example.boardgamerapp.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.boardgamerapp.ui.events.EventsScreen
import com.example.boardgamerapp.ui.events.EventsScreenWithPadding
import com.example.boardgamerapp.ui.games.GamesScreen
import com.example.boardgamerapp.ui.messages.MessagesScreen
import com.example.boardgamerapp.ui.theme.BoardGamerTheme

@Composable
fun MainScreen() {
    BoardGamerTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "events",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("events") { EventsScreenWithPadding(innerPadding) }
                composable("games") { GamesScreen() }
                composable("messages") { MessagesScreen() }
            }
        }
    }
}