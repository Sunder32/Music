package com.example.musicai.data.api

import com.example.musicai.data.model.MusicRequest
import com.example.musicai.data.model.MusicResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun generateMusic(
        @Body request: MusicRequest
    ): Response<MusicResponse>
}
