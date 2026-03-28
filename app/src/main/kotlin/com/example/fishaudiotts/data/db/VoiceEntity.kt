package com.example.fishaudiotts.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing favorite voices in local database
 */
@Entity(tableName = "favorite_voices")
data class VoiceEntity(
    @PrimaryKey
    val id: String,
    val nickname: String,
    val referenceId: String,
    val emotion: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
