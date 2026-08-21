package com.example.trailmix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.trailmix.screen.TrailMixApp
import com.example.trailmix.ui.theme.TrailMixTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrailMixTheme {
                TrailMixApp()
            }
        }
        window.navigationBarColor =
            getColor(com.example.trailmix.R.color.black)
    }
}

//package com.example.trailmix
//
//import android.Manifest
//import android.media.MediaPlayer
//import android.os.Build
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.core.net.toUri
//import androidx.lifecycle.lifecycleScope
//import com.example.trailmix.data.*
//import com.example.trailmix.ui.theme.TrailMixTheme
//import kotlinx.coroutines.launch
//
//class MainActivity : ComponentActivity() {
//
//    private val permissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { granted ->
//            if (granted) {
//                scanMusic()
//            }
//        }
//
//    private var songCount by mutableIntStateOf(0)
//    private var songs by mutableStateOf(listOf<Song>())
//    private var newSongs by mutableStateOf(listOf<Song>())
//
//    private var songloc by mutableIntStateOf(0)
//
//    private var databaseSongs by mutableStateOf(listOf<Song>())
//    private var databaseNewSongs by mutableStateOf(listOf<Song>())
//    private var selectedSongs by mutableStateOf(setOf<String>())
//
//    private var showDatabase by mutableStateOf(false)
//
//    private var mediaPlayer: MediaPlayer? = null
//
//    private lateinit var database: TrailMixDatabase
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        database = TrailMixDatabase.getDatabase(this)
//
//        loadSongs()
//
//        setContent {
//            TrailMixTheme {
//
//                if (showDatabase) {
//
//                    DatabaseScreen(
//                        songs = databaseSongs,
//                        newSongs = databaseNewSongs,
//                        selectedSongs = selectedSongs,
//
//                        onToggle = { song ->
//                            selectedSongs =
//                                if (selectedSongs.contains(song.uri)) {
//                                    selectedSongs - song.uri
//                                } else {
//                                    selectedSongs + song.uri
//                                }
//                        },
//
//                        onSave = {
//                            saveDatabase()
//                        }
//                    )
//
//                } else {
//
//                    TrailMixScreen(
//                        songCount = songCount,
//                        songs = songs,
//                        songloc = songloc,
//
//                        onRequestPermission = {
//                            requestMusicPermission()
//                        },
//
//                        onDatabase = {
//                            openDatabase()
//                        },
//
//                        inc = {
//                            inc()
//                        },
//
//                        dec = {
//                            dec()
//                        },
//
//                        empty = {
//                            empty()
//                        },
//
//                        import = {
//                            importSongs()
//                        },
//
//                        play = {
//                            play()
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    private fun requestMusicPermission() {
//
//        val permission = if (Build.VERSION.SDK_INT >= 33) {
//            Manifest.permission.READ_MEDIA_AUDIO
//        } else {
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        }
//
//        permissionLauncher.launch(permission)
//    }
//
//    private fun scanMusic() {
//
//        lifecycleScope.launch {
//
//            val scanner = LibraryScanner(
//                this@MainActivity,
//                database
//            )
//
//            val result = scanner.scan()
//
//            newSongs = result.newSongs
//
//            println("NEW SONGS: ${newSongs.size}")
//        }
//    }
//
//    private fun openDatabase() {
//
//        lifecycleScope.launch {
//
//            val scanner = LibraryScanner(
//                this@MainActivity,
//                database
//            )
//
//            val result = scanner.scan()
//
//            databaseSongs = database.songDao().getAllSongs()
//            databaseNewSongs = result.newSongs
//
//            selectedSongs =
//                databaseSongs.map { it.uri }.toSet() +
//                        databaseNewSongs.map { it.uri }.toSet()
//
//            showDatabase = true
//        }
//    }
//
//    private fun saveDatabase() {
//
//        lifecycleScope.launch {
//
//            /*
//             * New songs:
//             * checked = import
//             * unchecked = ignore
//             */
//            val selectedNewSongs =
//                databaseNewSongs.filter {
//                    selectedSongs.contains(it.uri)
//                }
//
//            if (selectedNewSongs.isNotEmpty()) {
//                database.songDao().insertAll(selectedNewSongs)
//            }
//
//            /*
//             * Existing songs:
//             * checked = available
//             * unchecked = missing
//             */
//            for (song in databaseSongs) {
//
//                val updatedSong = song.copy(
//                    missing = !selectedSongs.contains(song.uri)
//                )
//
//                database.songDao().update(updatedSong)
//            }
//
//            songs = database.songDao().getAllSongs()
//            songCount = songs.size
//
//            databaseSongs = emptyList()
//            databaseNewSongs = emptyList()
//            selectedSongs = emptySet()
//
//            showDatabase = false
//        }
//    }
//
//    fun inc() {
//        if (songloc < songs.size - 1) {
//            songloc++
//        }
//    }
//
//    fun dec() {
//        if (songloc > 0) {
//            songloc--
//        }
//    }
//
//    fun empty() {
//
//        lifecycleScope.launch {
//
//            database.songDao().deleteAll()
//
//            songs = emptyList()
//            songCount = 0
//            songloc = 0
//        }
//    }
//
//    fun play() {
//        if (songs.isEmpty()) {
//            return
//        }
//
//        if (songloc >= songs.size) {
//            songloc = 0
//        }
//
//        mediaPlayer?.release()
//
//        mediaPlayer = MediaPlayer.create(
//            this,
//            songs[songloc].uri.toUri()
//        )
//
//        mediaPlayer?.start()
//    }
//
//    private fun loadSongs() {
//
//        lifecycleScope.launch {
//
//            songs = database.songDao().getAllSongs()
//            songCount = songs.size
//        }
//    }
//
//    private fun importSongs() {
//
//        lifecycleScope.launch {
//
//            database.songDao().insertAll(newSongs)
//
//            newSongs = emptyList()
//
//            songs = database.songDao().getAllSongs()
//            songCount = songs.size
//        }
//    }
//}
//
//
//@Composable
//fun TrailMixScreen(
//    songCount: Int,
//    songs: List<Song>,
//    songloc: Int,
//
//    onRequestPermission: () -> Unit,
//    onDatabase: () -> Unit,
//
//    inc: () -> Unit,
//    dec: () -> Unit,
//    play: () -> Unit,
//    empty: () -> Unit,
//    import: () -> Unit
//) {
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//
//        Text("TrailMix")
//
//        Text("Found $songCount songs")
//
//        Button(
//            onClick = onRequestPermission
//        ) {
//            Text("Scan Music")
//        }
//
//        Button(
//            onClick = onDatabase
//        ) {
//            Text("Database")
//        }
//
//        if (songs.isNotEmpty()) {
//
//            Text(
//                "Song #${songloc + 1}: ${songs[songloc].title}\n" +
//                        "${songs[songloc].duration} long"
//            )
//        }
//
//        Button(
//            onClick = inc
//        ) {
//            Text("Move Up")
//        }
//
//        Button(
//            onClick = dec
//        ) {
//            Text("Move Down")
//        }
//
//        Button(
//            onClick = play
//        ) {
//            Text("Play")
//        }
//
//        Button(
//            onClick = empty
//        ) {
//            Text("del")
//        }
//
//        Button(
//            onClick = import
//        ) {
//            Text("import")
//        }
//    }
//}
//
//
//@Composable
//fun DatabaseScreen(
//    songs: List<Song>,
//    newSongs: List<Song>,
//    selectedSongs: Set<String>,
//
//    onToggle: (Song) -> Unit,
//    onSave: () -> Unit
//) {
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Text(
//            text = "Database",
//            modifier = Modifier.padding(16.dp)
//        )
//
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth()
//        ) {
//
//            items(songs) { song ->
//
//                SongRow(
//                    song = song,
//                    checked = selectedSongs.contains(song.uri),
//                    onToggle = {
//                        onToggle(song)
//                    }
//                )
//            }
//
//            if (newSongs.isNotEmpty()) {
//
//                item {
//
//                    Text(
//                        text = "────── New ──────",
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//
//                items(newSongs) { song ->
//
//                    SongRow(
//                        song = song,
//                        checked = selectedSongs.contains(song.uri),
//                        onToggle = {
//                            onToggle(song)
//                        }
//                    )
//                }
//            }
//        }
//
//        Button(
//            onClick = onSave,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text("SAVE")
//        }
//    }
//}
//
//
//@Composable
//fun SongRow(
//    song: Song,
//    checked: Boolean,
//    onToggle: () -> Unit
//) {
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable {
//                onToggle()
//            }
//            .padding(8.dp),
//
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        Text(
//            text = song.title,
//            modifier = Modifier
//                .weight(1f)
//                .padding(start = 8.dp)
//        )
//
//        Checkbox(
//            checked = checked,
//            onCheckedChange = {
//                onToggle()
//            }
//        )
//    }
//}