package com.example.trailmix

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.trailmix.ui.theme.TrailMixTheme
import com.example.trailmix.data.MusicScanner
//import com.example.trailmix.data.LibraryScanner
import com.example.trailmix.data.*
//import com.example.trailmix.data.Song
//import com.example.trailmix.data.TrailMixDatabase
import android.media.MediaPlayer
import androidx.core.net.toUri

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                scanMusic()
            }
        }

    private var songCount by mutableIntStateOf(0)
    private var songs by mutableStateOf(listOf<Song>())
    private var songloc by mutableIntStateOf(0)
    private var mediaPlayer: MediaPlayer? = null
    private var newSongs by mutableStateOf(listOf<Song>())
    private lateinit var database: TrailMixDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = TrailMixDatabase.getDatabase(this)

        loadSongs()

        setContent {
            TrailMixTheme {
                TrailMixScreen(
                    songCount = songCount,
                    songs = songs,
                    songloc = songloc,
                    onRequestPermission = {
                        requestMusicPermission()
                    },
                    inc = {
                        inc()
                    },
                    dec = {
                        dec()
                    },
                    empty = {
                        empty()
                    },
                    import = {
                        importSongs()
                    },
                    play = {
                        play()
                    }
                )
            }
        }
    }

    private fun requestMusicPermission() {

        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        permissionLauncher.launch(permission)
    }

    private fun scanMusic() {
        lifecycleScope.launch {

            val scanner = LibraryScanner(
                this@MainActivity,
                database
            )

            val result = scanner.scan()

            newSongs = result.newSongs

            println("NEW SONGS: ${newSongs.size}")
        }
    }

    fun inc() {
        if (songloc < songs.size - 1) {
            songloc++
        }
    }

    fun dec() {
        if (songloc > 0) {
            songloc--
        }
    }
    fun empty() {
        lifecycleScope.launch {
            database.songDao().deleteAll()

            songs = emptyList()
            songCount = 0
            songloc = 0
        }
    }
    fun play(){
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(
            this,
            songs[songloc].uri.toUri()
        )

        mediaPlayer?.start()
    }
    private fun loadSongs() {
        lifecycleScope.launch {
            songs = database.songDao().getAllSongs()
            songCount = songs.size
        }
    }
    private fun importSongs() {
        lifecycleScope.launch {

            database.songDao().insertAll(newSongs)
            newSongs = emptyList()

            songs = database.songDao().getAllSongs()
            songCount = songs.size
        }
    }
}


@Composable
fun TrailMixScreen(
    songCount: Int,
    songs: List<Song>,
    songloc: Int,
    onRequestPermission: () -> Unit,
    inc: () -> Unit,
    dec: () -> Unit,
    play: () -> Unit,
    empty: () -> Unit,
    import: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("TrailMix")

        Text("Found $songCount songs")

        Button(
            onClick = onRequestPermission
        ) {
            Text("Scan Music")
        }
        if (songs.isNotEmpty()) {
            Text("Song #$songloc: ${songs[songloc].title}\n ${songs[songloc].duration} long")
        }
        Button(
            onClick = inc
        ) {
            Text("Move Up")
        }
        Button(
            onClick = dec
        ) {
            Text("Move Down")
        }
        Button(
            onClick = play
        ) {
            Text("Play")
        }
        Button(
            onClick = empty
        ) {
            Text("del")
        }
        Button(
            onClick = import
        ) {
            Text("import")
        }
    }
}

