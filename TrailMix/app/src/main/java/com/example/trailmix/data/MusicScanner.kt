package com.example.trailmix.data

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

class MusicScanner(
    private val context: Context
) {

    @RequiresApi(Build.VERSION_CODES.R)
    fun scan(): List<Song> {

        val songs = mutableListOf<Song>()

        val collection =
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val cursor = context.contentResolver.query(
            collection,
            projection,
            null,//"${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        cursor?.use {

            val idColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media._ID
                )

            val titleColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.TITLE
                )

            val artistColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.ARTIST
                )

            val albumColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.ALBUM
                )

            val durationColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.DURATION
                )

            while (it.moveToNext()) {

                val id = it.getLong(idColumn)

                val uri =
                    MediaStore.Audio.Media
                        .getContentUri(
                            MediaStore.VOLUME_EXTERNAL,
                            id
                        )
                        .toString()

                songs.add(
                    Song(
                        uri = uri,
                        title = it.getString(titleColumn),
                        artist = it.getString(artistColumn),
                        album = it.getString(albumColumn),
                        duration = it.getLong(durationColumn)
                    )
                )
            }
        }

        return songs
    }
}