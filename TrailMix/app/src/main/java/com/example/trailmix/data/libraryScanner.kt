package com.example.trailmix.data

import android.content.Context

class LibraryScanner(
    private val context: Context,
    private val database: TrailMixDatabase
) {

    suspend fun scan(): ScanResult {

        val mediaStoreSongs = MusicScanner(context).scan()
        val databaseSongs = database.songDao().getAllSongs()

        val mediaStoreByUri = mediaStoreSongs.associateBy { it.uri }

        val newSongs = mutableListOf<Song>()
        val missingSongs = mutableListOf<Song>()
        val restoredSongs = mutableListOf<Song>()

        // Find new and restored songs
        for (song in mediaStoreSongs) {

            val existing = databaseSongs.find {
                it.uri == song.uri
            }

            if (existing == null) {
                newSongs.add(song)
            } else if (existing.missing) {

                val restored = existing.copy(
                    missing = false
                )

                database.songDao().update(restored)
                restoredSongs.add(restored)
            }
        }

        // Find missing songs
        for (song in databaseSongs) {

            if (!mediaStoreByUri.containsKey(song.uri) && !song.missing) {

                val missing = song.copy(
                    missing = true
                )

                database.songDao().update(missing)
                missingSongs.add(missing)
            }
        }

        return ScanResult(
            newSongs = newSongs,
            missingSongs = missingSongs,
            restoredSongs = restoredSongs
        )
    }
}