package com.example.boardgamerapp.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.boardgamerapp.data.database.GameDao
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameVote
import kotlinx.coroutines.flow.Flow
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameRepo(private val gameDao: GameDao) {
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val nextGame: Flow<Game?> = gameDao.getNextGame(formatDateForDB(currentDate))
    val upcomingGames: Flow<List<Game>?> = gameDao.getUpcomingGames(formatDateForDB(currentDate))
    val pastGames: Flow<List<Game>?> = gameDao.getPastGames(formatDateForDB(currentDate))

    suspend fun insertGame(game: Game) {
        gameDao.insertGame(game)
    }

    suspend fun updateSuggestedGames(gameId: Int, newSuggestionsList: List<String>) {
        gameDao.updateSuggestedGames(gameId, newSuggestionsList)
    }

    suspend fun updateVote(gameVote: GameVote) {
        gameDao.voteForGame(gameVote)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun voteForGame(vote: GameVote) {
        gameDao.voteForGame(vote)
    }

    suspend fun hasUserVoted(gameId: Int, userId: String): Int {
        return gameDao.hasUserVoted(gameId, userId)
    }

    fun getGameVotes(gameId: Int): Flow<List<GameVote>?> {
        return gameDao.getVotesForGame(gameId)
    }

    fun getGameById(gameId: Int) {
        gameDao.getGameById(gameId)
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


}