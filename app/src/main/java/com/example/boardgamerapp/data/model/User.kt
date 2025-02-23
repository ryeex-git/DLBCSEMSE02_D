package com.example.boardgamerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val userName: String,
    val favoriteCuisine: String? = null
)
