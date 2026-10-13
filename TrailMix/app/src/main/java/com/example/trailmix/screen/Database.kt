package com.example.trailmix.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailmix.data.LibraryManager
import com.example.trailmix.data.Song
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun DatabaseScreen(
    libraryManager: LibraryManager,
    onSave: suspend (
        List<Song>,
        Set<Song>
    ) -> Unit
) {

    var songs by remember {
        mutableStateOf<List<Song>>(emptyList())
    }

    var newSongs by remember {
        mutableStateOf<List<Song>>(emptyList())
    }

    var selectedSongs by remember {
        mutableStateOf<Set<Song>>(emptySet())
    }

    var editingSong by remember {
        mutableStateOf<Song?>(null)
    }

    val scope = rememberCoroutineScope()

    var scanning by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        scanning = true

        val result = libraryManager.scan()

        songs = result.library
        newSongs = result.newSongs

        selectedSongs =
            songs.filter { it.show }.toSet() +
                    newSongs.filter { it.show }.toSet()

        scanning = false
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = "Database",
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            )

            if (scanning) {

                Text(
                    text = "Scanning music...",
                    fontSize = 15.sp
                )

            } else {

                Text(
                    text = "${songs.size + newSongs.size} songs found, ${newSongs.size} new",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
                        horizontal = 20.dp
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {

                    item {
                        Text(
                            text = "New",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            )
                        )
                    }

                    if (newSongs.isNotEmpty()) {

                        items(
                            items = newSongs,
                            key = { "new_${it.uri}" }
                        ) { song ->

                            DatabaseSongRow(
                                song = song,
                                checked = selectedSongs.contains(song),
                                onCheckedChange = {

                                    selectedSongs =
                                        if (selectedSongs.contains(song)) {
                                            selectedSongs - song
                                        } else {
                                            selectedSongs + song
                                        }
                                },
                                onEdit = {
                                    editingSong = it
                                }
                            )
                        }

                    } else {

                        item {
                            Text(
                                text = "No New Songs... Find Some more!",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 8.dp
                                )
                            )
                        }
                    }

                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                vertical = 12.dp
                            )
                        )
                    }

                    item {
                        Text(
                            text = "Processed",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            )
                        )
                    }

                    items(
                        items = songs,
                        key = { "library_${it.uri}" }
                    ) { song ->

                        DatabaseSongRow(
                            song = song,
                            checked = selectedSongs.contains(song),
                            onCheckedChange = {

                                selectedSongs =
                                    if (selectedSongs.contains(song)) {
                                        selectedSongs - song
                                    } else {
                                        selectedSongs + song
                                    }
                            },
                            onEdit = {
                                editingSong = it
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {

                            onSave(
                                newSongs,
                                selectedSongs
                            )

                            val result =
                                libraryManager.scan()

                            songs = result.library
                            newSongs = result.newSongs

                            selectedSongs =
                                songs.filter { it.show }.toSet() +
                                        newSongs.filter { it.show }.toSet()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("SAVE")
                }
            }
        }

        if (editingSong != null) {

            var title by remember(editingSong) {
                mutableStateOf(editingSong!!.title)
            }

            var artist by remember(editingSong) {
                mutableStateOf(editingSong!!.artist ?: "")
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(
                            alpha = 0.5f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Edit Song",
                            fontSize = 24.sp
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                            },
                            label = {
                                Text("Title")
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        )

                        OutlinedTextField(
                            value = artist,
                            onValueChange = {
                                artist = it
                            },
                            label = {
                                Text("Artist")
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {

                            TextButton(
                                onClick = {
                                    editingSong = null
                                }
                            ) {
                                Text("CANCEL")
                            }

                            Button(
                                onClick = {
                                    scope.launch {

                                        val editedSong =
                                            editingSong!!.copy(
                                                title = title.ifBlank {
                                                    "Empty"
                                                },
                                                artist = artist.ifBlank {
                                                    null
                                                }
                                            )

                                        libraryManager.updateSong(
                                            editedSong
                                        )

                                        songs =
                                            songs.map {
                                                if (it.uri == editedSong.uri) {
                                                    editedSong
                                                } else {
                                                    it
                                                }
                                            }

                                        newSongs =
                                            newSongs.map {
                                                if (it.uri == editedSong.uri) {
                                                    editedSong
                                                } else {
                                                    it
                                                }
                                            }

                                        selectedSongs =
                                            selectedSongs.map {
                                                if (it.uri == editedSong.uri) {
                                                    editedSong
                                                } else {
                                                    it
                                                }
                                            }.toSet()

                                        editingSong = null
                                    }
                                }
                            ) {
                                Text("SAVE")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseSongRow(
    song: Song,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    onEdit: (Song) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChange()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = song.title,
                fontSize = 16.sp
            )

            Text(
                text = song.artist
                    ?.takeIf {
                        it.isNotBlank() &&
                                !it.equals(
                                    "<unknown>",
                                    ignoreCase = true
                                )
                    }
                    ?: "Not Specified",
                fontSize = 13.sp
            )
        }

        TextButton(
            onClick = {
                onEdit(song)
            }
        ) {
            Text("EDIT")
        }

        Checkbox(
            checked = checked,
            onCheckedChange = {
                onCheckedChange()
            }
        )
    }
}
