package com.example.trailmix.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val uri: String,

    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,

    val missing: Boolean = false
)