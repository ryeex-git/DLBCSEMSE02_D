package com.example.boardgamerapp.data.model

import androidx.room.Entity

@Entity(tableName = "game_ratings", primaryKeys = ["gameId", "userId"])
data class GameRating(
    val gameId: Int,
    val userId: String,
    val rating: Float
)