package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_events")
data class PersonalEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // "YYYY-MM-DD"
    val time: String? = null, // "HH:mm"
    val eventType: String = "PERSONAL", // BIRTHDAY, ANNIVERSARY, REMINDER, PERSONAL
    val colorHex: String = "#8B5CF6",
    val hasReminder: Boolean = false,
    val isCompleted: Boolean = false,
    val location: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_events")
data class FavoriteEntity(
    @PrimaryKey
    val eventId: String,
    val savedTimestamp: Long = System.currentTimeMillis(),
    val personalNotes: String? = null
)
