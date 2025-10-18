package com.example.musicai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.example.musicai.data.api.RetrofitInstance
import com.example.musicai.data.repository.MusicRepository
import com.example.musicai.ui.screens.MusicAppScreen
import com.example.musicai.ui.theme.MusicAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MusicAiTheme {
                MusicAppScreen()
            }
        }
    }
}