package com.example.trailmix.data

data class ScanResult(
    val newSongs: List<Song>,
    val missingSongs: List<Song>,
    val restoredSongs: List<Song>
)