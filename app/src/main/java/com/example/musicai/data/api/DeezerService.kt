package com.example.musicai.data.api

import com.example.musicai.data.models.DeezerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): DeezerResponse
}
