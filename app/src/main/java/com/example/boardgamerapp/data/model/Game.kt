package com.example.boardgamerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val suggestedGames: List<String> = emptyList(),
    val hostId: String
)