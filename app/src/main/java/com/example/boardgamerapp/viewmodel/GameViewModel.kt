package com.example.boardgamerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.boardgamerapp.data.database.GameDatabase
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameVote
import com.example.boardgamerapp.data.repository.GameRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepo
    val nextGame: Flow<Game?>
    val allUpcomingGames: Flow<List<Game>?>
    val allPastGames: Flow<List<Game>?>

    init {
        val gameDao = GameDatabase.getDatabase(application).gameDao()
        repository = GameRepo(gameDao)
        nextGame = repository.nextGame
        allUpcomingGames = repository.upcomingGames
        allPastGames = repository.pastGames
    }

    fun addGame(game: Game) {
        viewModelScope.launch {
            repository.insertGame(game)
        }
    }

    fun voteForGame(vote: GameVote) {
        viewModelScope.launch {
            val hasVoted = repository.hasUserVoted(vote.gameId, vote.userId) > 0
            if (!hasVoted) {
                repository.voteForGame(vote)
            }
        }
    }

    fun getGameVotes(gameId: Int): Flow<List<GameVote>?> {
        return repository.getGameVotes(gameId)
    }

    fun updateGameSuggestions(gameId: Int, newSuggestions: List<String>) {
        viewModelScope.launch {
            repository.updateSuggestedGames(gameId, newSuggestions)
        }
    }

    fun getGameById(gameId: Int) {
        viewModelScope.launch {
            repository.getGameById(gameId)
        }
    }

    fun submitGameRating(gameId: Int, userId: String, rating: Float) {
        viewModelScope.launch {
            repository.submitUserRating(gameId, userId, rating)
        }
    }

    fun getUserRating(gameId: Int, userId: String): StateFlow<Float?> {
        val ratingFlow = MutableStateFlow<Float?>(null)
        viewModelScope.launch {
            ratingFlow.value = repository.getUserRating(gameId, userId)
        }
        return ratingFlow
    }

    fun getAverageGameRating(gameId: Int): Flow<Float?> {
        return repository.getAverageRating(gameId)
    }
}