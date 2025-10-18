package com.example.musicai.data.models

import com.google.gson.annotations.SerializedName

data class DeezerResponse(
    val data: List<DeezerTrack>
)

data class DeezerTrack(
    val id: Long,
    val title: String,
    @SerializedName("title_short")
    val titleShort: String?,
    val link: String,
    val duration: Int, // в секундах
    val rank: Int,
    val preview: String, // URL превью (30 сек)
    val artist: DeezerArtist,
    val album: DeezerAlbum
)

data class DeezerArtist(
    val id: Long,
    val name: String,
    @SerializedName("picture")
    val picture: String?,
    @SerializedName("picture_small")
    val pictureSmall: String?,
    @SerializedName("picture_medium")
    val pictureMedium: String?,
    @SerializedName("picture_big")
    val pictureBig: String?,
    val tracklist: String
)

data class DeezerAlbum(
    val id: Long,
    val title: String,
    @SerializedName("cover")
    val cover: String?,
    @SerializedName("cover_small")
    val coverSmall: String?,
    @SerializedName("cover_medium")
    val coverMedium: String?,
    @SerializedName("cover_big")
    val coverBig: String?,
    @SerializedName("cover_xl")
    val coverXl: String?
)
