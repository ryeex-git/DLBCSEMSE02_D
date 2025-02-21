package com.example.boardgamerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.boardgamerapp.data.model.Game
import com.example.boardgamerapp.data.model.GameRating
import com.example.boardgamerapp.data.model.GameVote
import com.example.boardgamerapp.utils.Converters

@Database(
    entities = [Game::class, GameVote::class, GameRating::class],
    version = 8,
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}