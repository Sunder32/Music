package com.example.musicai.utils

object DemoMusic {
    // Ссылки на бесплатную демо-музыку
    val demoTracks = mapOf(
        "Электроника" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Спокойное" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Энергичное" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    
    fun getDemoTrack(genre: String, mood: String): String {
        return demoTracks[genre] ?: demoTracks[mood] 
            ?: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    }
}
