package com.example.boardgamerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_ratings")
data class GameRating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameId: Int,
    val userId: String,
    val rating: Float
)