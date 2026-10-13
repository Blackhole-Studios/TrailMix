package com.example.trailmix.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.trailmix.data.LibraryManager
import com.example.trailmix.R

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
                            painter = painterResource(
                                if (currentScreen == TrailMixScreen.LIBRARY)
                                    R.drawable.library_filled
                                else
                                    R.drawable.library_unfilled
                            ),
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
                            painter = painterResource(
                                R.drawable.search
                            ),
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
                            painter = painterResource(
                                if (currentScreen == TrailMixScreen.HOME)
                                    R.drawable.home_filled
                                else
                                    R.drawable.home_unfilled
                            ),
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
                            painter = painterResource(
                                if (currentScreen == TrailMixScreen.DATABASE)
                                    R.drawable.database_filled
                                else
                                    R.drawable.database_unfilled
                            ),
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
                            painter = painterResource(
                                if (currentScreen == TrailMixScreen.SETTINGS)
                                    R.drawable.settings_filled
                                else
                                    R.drawable.settings_unfilled
                            ),
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