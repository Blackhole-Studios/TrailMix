package com.example.trailmix.data

data class ScanResult(
    val library: List<Song>,
    val newSongs: List<Song>,
    val missingSongs: List<Song>,
    val restoredSongs: List<Song>
)