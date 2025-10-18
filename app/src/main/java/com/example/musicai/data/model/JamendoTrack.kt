package com.example.musicai.data.model

import com.google.gson.annotations.SerializedName

data class JamendoResponse(
    @SerializedName("results")
    val results: List<JamendoTrack>
)

data class JamendoTrack(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("artist_name")
    val artistName: String,
    @SerializedName("album_name")
    val albumName: String?,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("audio")
    val audio: String,
    @SerializedName("audiodownload")
    val audioDownload: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("album_image")
    val albumImage: String?
)
