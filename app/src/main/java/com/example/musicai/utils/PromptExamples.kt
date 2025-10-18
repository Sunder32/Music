package com.example.musicai.utils

/**
 * Примеры промптов для генерации музыки
 */
object PromptExamples {
    
    val examples = listOf(
        Example(
            title = "Летний вайб",
            prompt = "Upbeat summer vibes with tropical house beats, perfect for beach parties",
            genre = "Electronic",
            mood = "Happy"
        ),
        Example(
            title = "Ночной город",
            prompt = "Dark synthwave with cyberpunk atmosphere, neon lights and rain",
            genre = "Electronic",
            mood = "Dark"
        ),
        Example(
            title = "Романтический вечер",
            prompt = "Soft piano melody with strings, intimate and emotional",
            genre = "Classical",
            mood = "Romantic"
        ),
        Example(
            title = "Энергичная тренировка",
            prompt = "Heavy electronic drops with aggressive bass, perfect for workout",
            genre = "Electronic",
            mood = "Energetic"
        ),
        Example(
            title = "Спокойное утро",
            prompt = "Gentle acoustic guitar with birds singing, peaceful morning atmosphere",
            genre = "Ambient",
            mood = "Calm"
        ),
        Example(
            title = "Эпическое приключение",
            prompt = "Orchestral masterpiece with powerful drums and heroic brass section",
            genre = "Classical",
            mood = "Epic"
        ),
        Example(
            title = "Lo-fi для учебы",
            prompt = "Chill lo-fi beats with vinyl crackle, perfect for studying or relaxing",
            genre = "Lo-fi",
            mood = "Calm"
        ),
        Example(
            title = "Джазовый вечер",
            prompt = "Smooth jazz with saxophone solo, late night bar atmosphere",
            genre = "Jazz",
            mood = "Romantic"
        ),
        Example(
            title = "Хип-хоп энергия",
            prompt = "Hard hitting trap beats with 808s, modern urban sound",
            genre = "Hip-Hop",
            mood = "Energetic"
        ),
        Example(
            title = "Грустная баллада",
            prompt = "Melancholic piano ballad with emotional strings, rainy day mood",
            genre = "Pop",
            mood = "Melancholic"
        )
    )
    
    data class Example(
        val title: String,
        val prompt: String,
        val genre: String,
        val mood: String
    )
    
    fun getRandomExample() = examples.random()
}
