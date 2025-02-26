package com.example.boardgamerapp.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.boardgamerapp.data.database.GameDao
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameRating
import com.example.boardgamerapp.data.model.GameVote
import com.example.boardgamerapp.data.model.Message
import com.example.boardgamerapp.data.model.User
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
    val getAllUsers: Flow<List<User>?> = gameDao.getAllUsers()

    suspend fun insertGame(game: Game) {
        gameDao.insertGame(game)
    }

    suspend fun updateSuggestedGames(gameId: Int, newSuggestionsList: List<String>) {
        gameDao.updateSuggestedGames(gameId, newSuggestionsList)
    }

    suspend fun updateVote(gameVote: GameVote) {
        gameDao.voteForGame(gameVote)
    }

    suspend fun getNextHost(): User? {
        val userId = gameDao.getNextHostUserId()
        if (userId !== null)
            return gameDao.getUserById(userId)
        return null
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
            outputFormat.format(date!!)
        } catch (e: ParseException) {
            inputDate
        }
    }

    suspend fun submitUserRating(gameId: Int, userId: String, rating: Float) {
        gameDao.insertOrUpdateGameRating(
            GameRating(
                gameId = gameId,
                userId = userId,
                rating = rating
            )
        )
    }

    suspend fun getUserRating(gameId: Int, userId: String): Float? {
        return gameDao.getUserRatingForGame(gameId, userId)
    }

    fun getAverageRating(gameId: Int): Flow<Float?> {
        return gameDao.getAverageRatingForGame(gameId)
    }

    suspend fun setFavoriteCuisine(userId: String, cuisine: String) {
        val existingUser = gameDao.getUserById(userId)
        val updatedUser = existingUser?.copy(favoriteCuisine = cuisine)
            ?: User(userId = userId, userName = "Unbekannt", favoriteCuisine = cuisine)
        gameDao.insertOrUpdateUser(updatedUser)
    }

    suspend fun getUserById(userId: String): User? {
        return gameDao.getUserById(userId)
    }

    suspend fun insertOrUpdateUser(userId: String): User? {
        val userNames = listOf(
            "Lena",
            "Max",
            "Sophia",
            "Jonas",
            "Emma",
            "Luca",
            "Hannah",
            "Finn",
            "Mia",
            "Noah",
            "Lea",
            "Ben",
            "Clara",
            "Elias",
            "Anna",
            "Paul",
            "Laura",
            "David",
            "Nina",
            "Tom"
        )

        gameDao.getUserById(userId)
            ?: gameDao.insertOrUpdateUser(
                User(
                    userId = userId,
                    userName = userNames.random(),
                    favoriteCuisine = ""
                )
            )

        return gameDao.getUserById(userId)
    }

    suspend fun insertMessage(message: Message) {
        gameDao.insertMessage(message)
    }

    fun getMessagesForUser(userId: String): Flow<List<Message>> {
        return gameDao.getMessagesForUser(userId)
    }
}