package com.example.musicai.data

import android.content.Context
import android.content.SharedPreferences
import com.example.musicai.data.model.MusicTrack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class FavoritesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "music_ai_favorites"
        private const val KEY_FAVORITES = "favorites_list"
    }
    
    fun saveFavorites(tracks: List<MusicTrack>) {
        val json = gson.toJson(tracks)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
        println("FavoritesManager: Saved ${tracks.size} tracks")
    }
    
    fun loadFavorites(): List<MusicTrack> {
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<MusicTrack>>() {}.type
            val tracks = gson.fromJson<List<MusicTrack>>(json, type)
            println("FavoritesManager: Loaded ${tracks.size} tracks")
            tracks
        } catch (e: Exception) {
            println("FavoritesManager: Error loading favorites: ${e.message}")
            emptyList()
        }
    }
    
    
    fun clearFavorites() {
        prefs.edit().remove(KEY_FAVORITES).apply()
        println("FavoritesManager: Cleared all favorites")
    }
}
