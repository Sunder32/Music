package com.example.musicai.data.api

import com.example.musicai.data.model.ITunesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// iTunes Search API - бесплатный, без ключа
interface ITunesService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 25
    ): Response<ITunesResponse>
}
