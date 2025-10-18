package com.example.musicai.data.repository

import android.content.Context
import android.util.Log
import com.example.musicai.data.FavoritesManager
import com.example.musicai.data.api.RetrofitInstance
import com.example.musicai.data.model.Message
import com.example.musicai.data.model.MusicRequest
import com.example.musicai.data.model.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MusicRepository(context: Context) {
    private val api = RetrofitInstance.api
    private val deezerApi = RetrofitInstance.deezerApi
    private val favoritesManager = FavoritesManager(context)
    private val favoritesList = mutableListOf<MusicTrack>()
    
    init {
        favoritesList.addAll(favoritesManager.loadFavorites())
        println("MusicRepository: Initialized with ${favoritesList.size} favorite tracks")
    }
    
    suspend fun searchMusic(
        query: String,
        genre: String,
        mood: String
    ): Result<List<MusicTrack>> = withContext(Dispatchers.IO) {
        try {
            val searchQuery = buildSearchQuery(query, genre, mood)
            
            println("=".repeat(60))
            println("MusicRepository: Starting search")
            println("  Input - query: '$query', genre: '$genre', mood: '$mood'")
            println("  Final search query: '$searchQuery'")
            println("=".repeat(60))
            
            println("MusicRepository: Using Deezer API")
            val response = deezerApi.searchTracks(query = searchQuery, limit = 50)
            
            println("MusicRepository: Found ${response.data.size} tracks")
            
            if (response.data.isEmpty()) {
                println("MusicRepository: No tracks found, trying simplified search")
                
                val fallbackQueries = listOf(
                    getEnglishGenre(genre),  
                    "top hits",              
                    "music"                  
                )
                
                for (fallbackQuery in fallbackQueries) {
                    println("MusicRepository: Trying fallback query: '$fallbackQuery'")
                    val retryResponse = deezerApi.searchTracks(query = fallbackQuery, limit = 50)
                    println("MusicRepository: Fallback '$fallbackQuery' found ${retryResponse.data.size} tracks")
                    
                    if (retryResponse.data.isNotEmpty()) {
                        val retryTracks = retryResponse.data.map { deezerTrack ->
                            MusicTrack(
                                id = deezerTrack.id.toString(),
                                title = deezerTrack.title,
                                description = "Исполнитель: ${deezerTrack.artist.name}\nАльбом: ${deezerTrack.album.title}",
                                genre = genre,
                                mood = mood,
                                duration = formatDuration(deezerTrack.duration),
                                audioUrl = deezerTrack.preview,
                                coverColor = generateColorForMood(mood),
                                isGeneratingAudio = false
                            )
                        }
                        return@withContext Result.success(retryTracks)
                    }
                }
                
                return@withContext Result.failure(
                    Exception("Не удалось найти треки. Проверьте подключение к интернету.")
                )
            }
            
            val tracks = response.data.map { deezerTrack ->
                MusicTrack(
                    id = deezerTrack.id.toString(),
                    title = deezerTrack.title,
                    description = "Исполнитель: ${deezerTrack.artist.name}\nАльбом: ${deezerTrack.album.title}",
                    genre = genre,
                    mood = mood,
                    duration = formatDuration(deezerTrack.duration),
                    audioUrl = deezerTrack.preview,
                    coverColor = generateColorForMood(mood),
                    isGeneratingAudio = false
                )
            }
            
            Result.success(tracks)
        } catch (e: Exception) {
            println("MusicRepository: Error - ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Ошибка поиска: ${e.message ?: "Проверьте подключение к интернету"}"))
        }
    }
    
    fun addToFavorites(track: MusicTrack) {
        if (!favoritesList.any { it.id == track.id }) {
            favoritesList.add(track)
            favoritesManager.saveFavorites(favoritesList)
            println("MusicRepository: Added track '${track.title}' to favorites")
        }
    }
    
    fun removeFromFavorites(trackId: String) {
        favoritesList.removeAll { it.id == trackId }
        favoritesManager.saveFavorites(favoritesList)
        println("MusicRepository: Removed track with id '$trackId' from favorites")
    }
    
    fun isFavorite(trackId: String): Boolean {
        return favoritesList.any { it.id == trackId }
    }
    
    fun getFavorites(): List<MusicTrack> {
        return favoritesList.toList()
    }
    
    private fun buildSearchQuery(prompt: String, genre: String, mood: String): String {
        val genreMap = mapOf(
            "Электроника" to "electronic",
            "Хип-хоп" to "hip hop",
            "Рок" to "rock",
            "Джаз" to "jazz",
            "Классика" to "classical",
            "Эмбиент" to "ambient",
            "Поп" to "pop",
            "Lo-fi" to "lofi"
        )
        
        val moodMap = mapOf(
            "Энергичное" to "dance",
            "Спокойное" to "calm",
            "Счастливое" to "happy",
            "Меланхоличное" to "sad",
            "Грустное" to "sad",
            "Романтичное" to "love",
            "Темное" to "dark",
            "Эпичное" to "epic",
            "Мечтательное" to "dream"
        )
        
        if (prompt.isNotBlank()) {
            println("MusicRepository: Using direct search query: '$prompt'")
            return prompt.trim()
        }
        
        val genreQuery = genreMap[genre] ?: genre.lowercase()
        
        if (genreQuery.isBlank() || genreQuery == genre.lowercase() && genre.isBlank()) {
            return "top hits"
        }
        
        return genreQuery
    }
    
    private fun getEnglishGenre(genre: String): String {
        val genreMap = mapOf(
            "Электроника" to "electronic",
            "Хип-хоп" to "hip hop",
            "Рок" to "rock",
            "Джаз" to "jazz",
            "Классика" to "classical",
            "Эмбиент" to "ambient",
            "Поп" to "pop",
            "Lo-fi" to "lofi"
        )
        val result = genreMap[genre] ?: genre.lowercase()
        return if (result.isBlank()) "pop" else result
    }
    
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }
    
    suspend fun generateMusic(
        prompt: String,
        genre: String,
        mood: String
    ): Result<MusicTrack> = withContext(Dispatchers.IO) {
        try {
            val fullPrompt = """
                Generate a creative music track concept based on this description:
                Description: $prompt
                Genre: $genre
                Mood: $mood
                
                Please provide:
                1. A catchy track title
                2. A brief description of the music style and instrumentation
                3. The overall vibe and feeling
                
                Keep it concise and creative.
            """.trimIndent()
            
            val request = MusicRequest(
                model = "meta-llama/llama-3.2-3b-instruct:free",
                messages = listOf(
                    Message(role = "user", content = fullPrompt)
                ),
                maxTokens = 300
            )
            
            val response = api.generateMusic(request)
            
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content 
                    ?: "Untitled Track"
                
                val track = MusicTrack(
                    id = response.body()!!.id,
                    title = extractTitle(content),
                    description = content,
                    genre = genre,
                    mood = mood,
                    duration = "${Random.nextInt(2, 5)}:${Random.nextInt(10, 59)}",
                    coverColor = generateColorForMood(mood),
                    audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${Random.nextInt(1, 16)}.mp3",
                    isGeneratingAudio = false
                )
                Result.success(track)
            } else {
                Result.failure(Exception("Failed to generate music: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun extractTitle(content: String): String {
        val lines = content.lines()
        for (line in lines) {
            if (line.contains("title", ignoreCase = true) && line.contains(":")) {
                return line.substringAfter(":").trim().replace("\"", "")
            }
        }
        return lines.firstOrNull()?.take(50)?.trim() ?: "Untitled Track"
    }
    
    private fun generateColorForMood(mood: String): Int {
        return when (mood.lowercase()) {
            "счастливое", "happy" -> 0xFFFFD700.toInt()
            "энергичное", "energetic" -> 0xFFFF4500.toInt()
            "спокойное", "calm" -> 0xFF87CEEB.toInt()
            "меланхоличное", "melancholic" -> 0xFF4B0082.toInt()
            "романтичное", "romantic" -> 0xFFFF69B4.toInt()
            "темное", "dark" -> 0xFF2F4F4F.toInt()
            "эпичное", "epic" -> 0xFF8B0000.toInt()
            else -> 0xFF9370DB.toInt()
        }
    }
    
}


