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

            if (result.newSongs.size >= 3) {
                // Show import UI
            }

            songs = database.songDao().getAllSongs()
            songCount = songs.size
        }
    }
    fun inc(){
        songloc++
    }
    fun dec(){
        songloc--
    }
    fun empty(){
        songs = emptyList()
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
    empty: () -> Unit
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
            Text("Song #$songloc: ${songs[songloc].title}")
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
    }
}

