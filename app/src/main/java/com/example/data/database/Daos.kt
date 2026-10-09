package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalEventDao {
    @Query("SELECT * FROM personal_events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE date = :dateString ORDER BY time ASC")
    fun getEventsForDate(dateString: String): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE date LIKE :monthPrefix || '%' ORDER BY date ASC")
    fun getEventsForMonth(monthPrefix: String): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): PersonalEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: PersonalEventEntity): Long

    @Update
    suspend fun updateEvent(event: PersonalEventEntity)

    @Delete
    suspend fun deleteEvent(event: PersonalEventEntity)

    @Query("UPDATE personal_events SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_events ORDER BY savedTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_events WHERE eventId = :eventId)")
    fun isFavoriteFlow(eventId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_events WHERE eventId = :eventId)")
    suspend fun isFavorite(eventId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_events WHERE eventId = :eventId")
    suspend fun removeFavorite(eventId: String)

    @Query("UPDATE favorite_events SET personalNotes = :notes WHERE eventId = :eventId")
    suspend fun updateNotes(eventId: String, notes: String?)
}
