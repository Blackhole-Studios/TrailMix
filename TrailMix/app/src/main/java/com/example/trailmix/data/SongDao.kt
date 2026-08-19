package com.example.trailmix.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Query("SELECT * FROM songs WHERE uri = :uri LIMIT 1")
    suspend fun getSongByUri(uri: String): Song?

    @Insert
    suspend fun insert(song: Song)

    @Insert
    suspend fun insertAll(songs: List<Song>)

    @Update
    suspend fun update(song: Song)
}