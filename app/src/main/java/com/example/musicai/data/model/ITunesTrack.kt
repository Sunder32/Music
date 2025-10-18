package com.example.musicai.data.model

import com.google.gson.annotations.SerializedName

data class ITunesResponse(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<ITunesTrack>
)

data class ITunesTrack(
    @SerializedName("trackId")
    val trackId: Long,
    @SerializedName("trackName")
    val trackName: String?,
    @SerializedName("artistName")
    val artistName: String?,
    @SerializedName("collectionName")
    val collectionName: String?,
    @SerializedName("previewUrl")
    val previewUrl: String?,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String?,
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long?,
    @SerializedName("primaryGenreName")
    val primaryGenreName: String?,
    @SerializedName("releaseDate")
    val releaseDate: String?
)
