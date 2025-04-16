package com.example.scryptem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_coins")
data class FavoriteCoinEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val image: String,
    val currentPrice: Double
)
