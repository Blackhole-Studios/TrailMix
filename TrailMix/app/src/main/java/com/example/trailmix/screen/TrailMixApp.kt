package com.example.trailmix.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.trailmix.data.LibraryManager

enum class TrailMixScreen {
    LIBRARY,
    SEARCH,
    HOME,
    DATABASE,
    SETTINGS
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun TrailMixApp() {

    var currentScreen by remember {
        mutableStateOf(TrailMixScreen.HOME)
    }

    val context = LocalContext.current

    val libraryManager = remember {
        LibraryManager(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentScreen == TrailMixScreen.LIBRARY,
                    onClick = {
                        currentScreen = TrailMixScreen.LIBRARY
                    },
                    icon = {
                        Icon(
                            Icons.Default.LibraryMusic,
                            contentDescription = "Library"
                        )
                    },
                    label = {
                        Text("Library")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == TrailMixScreen.SEARCH,
                    onClick = {
                        currentScreen = TrailMixScreen.SEARCH
                    },
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = {
                        Text("Search")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == TrailMixScreen.HOME,
                    onClick = {
                        currentScreen = TrailMixScreen.HOME
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == TrailMixScreen.DATABASE,
                    onClick = {
                        currentScreen = TrailMixScreen.DATABASE
                    },
                    icon = {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = "Database"
                        )
                    },
                    label = {
                        Text("Database")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == TrailMixScreen.SETTINGS,
                    onClick = {
                        currentScreen = TrailMixScreen.SETTINGS
                    },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = {
                        Text("Settings")
                    }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (currentScreen) {

                TrailMixScreen.LIBRARY -> {
                    LibraryScreen(
                        libraryManager = libraryManager
                    )
                }

                TrailMixScreen.SEARCH -> {
                    SearchScreen()
                }

                TrailMixScreen.HOME -> {
                    HomeScreen()
                }

                TrailMixScreen.DATABASE -> {
                    DatabaseScreen(
                        libraryManager = libraryManager,
                        onSave = { newSongs, selectedSongs ->
                            libraryManager.processSongs(
                                newSongs,
                                selectedSongs
                            )
                        }
                    )
                }

                TrailMixScreen.SETTINGS -> {
                    SettingScreen()
                }
            }
        }
    }
}