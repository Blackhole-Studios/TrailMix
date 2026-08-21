# TrailMix ⛰️

TrailMix is an offline music player for Android (iOS later) built around the music already stored on your device.

The goal is simple: give local music the convenience of a modern streaming app without requiring a streaming service or an internet connection.

## Features

### Current

* Scan music stored on the device
* Maintain a local music database with Room
* Detect newly discovered songs
* Detect songs that are no longer available on the device
* Detect songs that return to the device
* Choose which songs appear in the library
* Edit song metadata

  * Title
  * Artist
  * Cover Art
* Browse the music currently included in the library
* Five-section navigation

  * Library
  * Search
  * Home
  * Database
  * Settings

### Planned

* Album artwork
* Search
* Playlists
* Recently played music
* Quick access
* Artist browsing
* Full-screen music player
* Compact player
* Playback queue
* Play/pause controls
* Listening statistics
* Bluetooth controls
* Notifications
* Custom accent colors
* Customizable home screen
* User profile

## Architecture

TrailMix currently separates the application into three main layers:

```text
UI
│
├── HomeScreen
├── LibraryScreen
├── SearchScreen
├── DatabaseScreen
└── SettingsScreen
        │
        ▼
LibraryManager
        │
        ├── MusicScanner
        │       └── Android MediaStore
        │
        └── SongDao
                └── Room Database
```

### MusicScanner

`MusicScanner` is responsible for reading music from Android's `MediaStore`.

It creates `Song` objects containing information such as:

* URI
* Title
* Artist
* Album
* Duration

It does not manage the application's database.

### LibraryManager

`LibraryManager` coordinates the music library.

It handles:

* Scanning the device
* Comparing MediaStore with the Room database
* Finding new songs
* Finding missing songs
* Detecting restored songs
* Processing newly discovered songs
* Updating song metadata
* Changing whether songs are shown in the library
* Clearing the database

### SongDao

`SongDao` provides the database operations used by `LibraryManager`.

The database stores each song as a `Song` entity.

## Song Model

Each song currently contains:

```kotlin
data class Song(
    val id: Long = 0,
    val uri: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val missing: Boolean = false,
    val show: Boolean = true
)
```

`missing` indicates that the song was previously known but is no longer found on the device, which prevents errors on request, it simply skips these objects.

`show` determines whether the song should appear in the user's music library.

This distinction allows TrailMix to remember songs that the user rejected without deleting them from the database.

## Database Workflow

When the Database screen opens, TrailMix scans the device.

```text
Android MediaStore
        │
        ▼
   MusicScanner
        │
        ▼
  LibraryManager
        │
        ├── New songs
        ├── Missing songs
        └── Restored songs
```

New songs appear under **New**.

Previously processed songs appear under **Processed**.

The checkbox controls the song's `show` property.

For example:

```text
☑ Song A
☐ Song B
☑ Song C
```

After saving:

```text
Song A → show = true
Song B → show = false
Song C → show = true
```

All songs remain in the database.

## Tech Stack

* Kotlin
* Android
* Jetpack Compose
* Material 3
* Room
* Android MediaStore
* Kotlin Coroutines

## Project Status

TrailMix is currently under active development.

The database and device scanning systems are being established before the rest of the player experience is built.

The current priority is getting the local music library pipeline reliable before adding larger features such as playlists and playback.

## License

License information has not yet been determined.
