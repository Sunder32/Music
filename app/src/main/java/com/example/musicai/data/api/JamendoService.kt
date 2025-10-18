package com.example.musicai.data.api

import com.example.musicai.data.model.JamendoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Jamendo API - бесплатная музыка
interface JamendoService {
    @GET("tracks")
    suspend fun searchTracks(
        @Query("client_id") clientId: String = "7d7cb8c2",
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("search") search: String,
        @Query("audioformat") audioFormat: String = "mp32"
    ): Response<JamendoResponse>
}
