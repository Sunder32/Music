package com.example.musicai.utils

object Constants {
    // API Configuration
    const val BASE_URL = "https://openrouter.ai/api/v1/"
    const val API_KEY = "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"
    const val MODEL = "meta-llama/llama-3.2-3b-instruct:free"
    
    // Genre Options
    val GENRES = listOf(
        "Electronic",
        "Hip-Hop",
        "Rock",
        "Jazz",
        "Classical",
        "Ambient",
        "Pop",
        "Lo-fi"
    )
    
    // Mood Options
    val MOODS = listOf(
        "Energetic",
        "Calm",
        "Happy",
        "Melancholic",
        "Romantic",
        "Dark",
        "Epic",
        "Dreamy"
    )
    
    // Mood Colors (ARGB)
    fun getMoodColor(mood: String): Int {
        return when (mood.lowercase()) {
            "happy" -> 0xFFFFD700.toInt()
            "energetic" -> 0xFFFF4500.toInt()
            "calm" -> 0xFF87CEEB.toInt()
            "melancholic" -> 0xFF4B0082.toInt()
            "romantic" -> 0xFFFF69B4.toInt()
            "dark" -> 0xFF2F4F4F.toInt()
            "epic" -> 0xFF8B0000.toInt()
            "dreamy" -> 0xFF9370DB.toInt()
            else -> 0xFF9370DB.toInt()
        }
    }
}
