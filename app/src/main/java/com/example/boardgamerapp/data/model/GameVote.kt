package com.example.boardgamerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_votes")
data class GameVote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameId: Int,
    val userId: String,
    val gameName: List<String> = emptyList()
)
