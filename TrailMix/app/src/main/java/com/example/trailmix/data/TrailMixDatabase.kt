package com.example.trailmix.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Song::class],
    version = 1,
    exportSchema = false
)
abstract class TrailMixDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    companion object {

        @Volatile
        private var INSTANCE: TrailMixDatabase? = null

        fun getDatabase(context: Context): TrailMixDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrailMixDatabase::class.java,
                    "trailmix_internal_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}