package com.example.boardgamerapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameRating
import com.example.boardgamerapp.data.model.GameVote
import com.example.boardgamerapp.data.model.Message
import com.example.boardgamerapp.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE date >= :currentDate ORDER BY date ASC, time ASC LIMIT 1")
    fun getNextGame(currentDate: String): Flow<Game?>

    @Query("SELECT * FROM games WHERE date >= :currentDate ORDER BY date ASC, time ASC")
    fun getUpcomingGames(currentDate: String): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE date < :currentDate ORDER BY date ASC, time ASC")
    fun getPastGames(currentDate: String): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameById(gameId: Int): Flow<Game?>

    @Query("UPDATE games SET suggestedGames = :newList WHERE id = :gameId")
    suspend fun updateSuggestedGames(gameId: Int, newList: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun voteForGame(vote: GameVote)

    @Query("SELECT * FROM game_votes WHERE gameId = :gameId")
    fun getVotesForGame(gameId: Int): Flow<List<GameVote>?>

    @Query("SELECT gameId, COUNT(*) as voteCount FROM game_votes GROUP BY gameId ORDER BY voteCount DESC")
    fun getGameVotes(): Flow<List<VoteResult>>

    @Query("SELECT COUNT(*) FROM game_votes WHERE gameId = :gameId AND userId = :userId")
    suspend fun hasUserVoted(gameId: Int, userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGameRating(rating: GameRating)

    @Query("SELECT rating FROM game_ratings WHERE gameId = :gameId AND userId = :userId")
    suspend fun getUserRatingForGame(gameId: Int, userId: String): Float?

    @Query("SELECT AVG(rating) FROM game_ratings WHERE gameId = :gameId")
    fun getAverageRatingForGame(gameId: Int): Flow<Float?>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(users: List<User>)

    @Query("SELECT hostId FROM games ORDER BY date DESC, time DESC LIMIT 1")
    suspend fun getLastHostUserId(): String?

    @Query("SELECT userId FROM users WHERE userId NOT IN (SELECT hostId FROM games) ORDER BY RANDOM() LIMIT 1")
    suspend fun getNextHostUserId(): String?

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    fun getMessagesForUser(userId: String): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
}

data class VoteResult(
    val gameId: Int,
    val voteCount: Int
)