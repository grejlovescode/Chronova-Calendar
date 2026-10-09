package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PersonalEventEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChronovaDatabase : RoomDatabase() {
    abstract fun personalEventDao(): PersonalEventDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: ChronovaDatabase? = null

        fun getDatabase(context: Context): ChronovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChronovaDatabase::class.java,
                    "chronova_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
