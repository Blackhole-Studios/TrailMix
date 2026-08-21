package com.example.trailmix.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailmix.data.LibraryManager
import com.example.trailmix.data.Song

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun LibraryScreen(
    libraryManager: LibraryManager
) {
    var songs by remember {
        mutableStateOf<List<Song>>(emptyList())
    }

    LaunchedEffect(Unit) {
        songs = libraryManager
            .getLibrary()
            .filter { it.show && !it.missing }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "Library",
            fontSize = 32.sp,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = songs,
                key = { it.uri }
            ) { song ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                ) {

                    Text(
                        text = song.title,
                        fontSize = 16.sp
                    )

                    Text(
                        text = song.artist
                            ?.takeIf {
                                it.isNotBlank() && !it.equals("<unknown>", ignoreCase = true)
                            }
                            ?: "Not Specified",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }


    }
}