package com.example.boardgamerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameRating
import com.example.boardgamerapp.data.model.GameVote
import com.example.boardgamerapp.data.model.Message
import com.example.boardgamerapp.data.model.User
import com.example.boardgamerapp.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [Game::class, GameVote::class, GameRating::class, User::class, Message::class],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                ).fallbackToDestructiveMigration().addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getDatabase(context).gameDao().insertMultiple(generateRandomUsers(10))
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        fun generateRandomUsers(count: Int): List<User> {
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

            return List(count) {
                val name = userNames.random()
                User(userName = name, userId = UUID.randomUUID().toString())
            }
        }
    }
}