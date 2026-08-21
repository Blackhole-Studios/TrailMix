package com.example.trailmix.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {

    @Query("SELECT * FROM songs ORDER BY title COLLATE NOCASE")
    suspend fun getAll(): List<Song>

    @Query("SELECT * FROM songs WHERE uri = :uri LIMIT 1")
    suspend fun getByUri(uri: String): Song?

    @Insert
    suspend fun insert(song: Song)

    @Insert
    suspend fun insertAll(songs: List<Song>)

    @Update
    suspend fun update(song: Song)

    @Update
    suspend fun updateAll(songs: List<Song>)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()
}