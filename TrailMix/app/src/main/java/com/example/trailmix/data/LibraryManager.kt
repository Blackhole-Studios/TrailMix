package com.example.trailmix.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class LibraryManager(
    context: Context
) {

    private val database =
        TrailMixDatabase.getDatabase(context)

    private val dao =
        database.songDao()

    private val scanner =
        MusicScanner(context)

    suspend fun getLibrary(): List<Song> {
        return dao.getAll()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun scan(): ScanResult {
        println("started Scan")

        val deviceSongs =
            scanner.scan()

        val librarySongs =
            dao.getAll()

        val libraryByUri =
            librarySongs.associateBy { it.uri }

        val deviceUris =
            deviceSongs.map { it.uri }.toSet()

        val newSongs =
            mutableListOf<Song>()

        val missingSongs =
            mutableListOf<Song>()

        val restoredSongs =
            mutableListOf<Song>()

        for (song in deviceSongs) {

            val existing =
                libraryByUri[song.uri]

            when {
                existing == null -> {
                    newSongs.add(song)
                }

                existing.missing -> {

                    val restored =
                        existing.copy(
                            missing = false
                        )

                    dao.update(restored)
                    restoredSongs.add(restored)
                }
            }
        }

        for (song in librarySongs) {

            if (
                song.uri !in deviceUris &&
                !song.missing
            ) {

                val missing =
                    song.copy(
                        missing = true
                    )

                dao.update(missing)
                missingSongs.add(missing)
            }
        }

        val updatedLibrary =
            dao.getAll()

        println("Scan Ended")
        return ScanResult(
            library = updatedLibrary,
            newSongs = newSongs,
            missingSongs = missingSongs,
            restoredSongs = restoredSongs
        )
    }

    suspend fun processSongs(
        newSongs: List<Song>,
        selectedSongs: Set<Song>
    ) {
        val selectedUris = selectedSongs
            .map { it.uri }
            .toSet()

        val processedNewSongs = newSongs.map { song ->
            song.copy(
                show = song.uri in selectedUris
            )
        }

        if (processedNewSongs.isNotEmpty()) {
            dao.insertAll(processedNewSongs)
        }

        val existingSongs = dao.getAll()

        val updatedSongs = existingSongs.map { song ->
            song.copy(
                show = song.uri in selectedUris
            )
        }

        if (updatedSongs.isNotEmpty()) {
            dao.updateAll(updatedSongs)
        }
    }

    suspend fun removeSongs(songs: List<Song>) {

        for (song in songs) {
            dao.update(
                song.copy(show = false)
            )
        }
    }

    suspend fun deleteEverything() {
        dao.deleteAll()
    }
    suspend fun updateSong(song: Song) {
        dao.update(song)
    }
}