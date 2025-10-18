package com.example.musicai.data.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// API для генерации музыки через HuggingFace
interface MusicGenService {
    @Headers("Content-Type: application/json")
    @POST("models/facebook/musicgen-small")
    suspend fun generateAudio(
        @Body request: RequestBody
    ): Response<ResponseBody>
}
