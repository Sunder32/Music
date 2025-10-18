package com.example.musicai.data.models

data class FavoriteTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val audioUrl: String,
    val albumArt: String,
    val addedAt: Long = System.currentTimeMillis()
)
