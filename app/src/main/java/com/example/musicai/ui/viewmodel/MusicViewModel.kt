package com.example.musicai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicai.data.model.MusicTrack
import com.example.musicai.data.repository.MusicRepository
import com.example.musicai.player.GlobalPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MusicUiState {
    object Idle : MusicUiState()
    object Loading : MusicUiState()
    data class Success(val tracks: List<MusicTrack>) : MusicUiState()
    data class Error(val message: String) : MusicUiState()
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val uiState: StateFlow<MusicUiState> = _uiState

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack

    private val _favoriteTracks = MutableStateFlow<Set<String>>(emptySet())
    val favoriteTracks: StateFlow<Set<String>> = _favoriteTracks
    
    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled
    
    private val _allTracks = MutableStateFlow<List<MusicTrack>>(emptyList())
    private val _isPlayingFromFavorites = MutableStateFlow(false)
    private val _playbackHistory = MutableStateFlow<List<String>>(emptyList())
    
    init {
        loadFavoritesIds()
        
        GlobalPlayerManager.setOnTrackEndedCallback {
            if (!_isRepeatEnabled.value) {
                playNext()
            }
        }
    }
    
    private fun loadFavoritesIds() {
        val favorites = repository.getFavorites()
        _favoriteTracks.value = favorites.map { it.id }.toSet()
        println("MusicViewModel: Loaded ${favorites.size} favorite track IDs")
    }
    
    private fun getCurrentPlaylist(): List<MusicTrack> {
        return if (_isPlayingFromFavorites.value) {
            when (val state = _uiState.value) {
                is MusicUiState.Success -> state.tracks
                else -> emptyList()
            }
        } else {
            _allTracks.value
        }
    }

    fun searchMusic(query: String, genre: String, mood: String) {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            _isPlayingFromFavorites.value = false  // Поиск - значит не из избранного
            try {
                val result = repository.searchMusic(query, genre, mood)
                result.onSuccess { tracks ->
                    _allTracks.value = tracks
                    _uiState.value = MusicUiState.Success(tracks)
                }.onFailure { e ->
                    _uiState.value = MusicUiState.Error(e.message ?: "Ошибка поиска")
                }
            } catch (e: Exception) {
                _uiState.value = MusicUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun selectTrack(track: MusicTrack) {
        _currentTrack.value = track
        _playbackHistory.value = _playbackHistory.value + track.id
    }
    

    fun playTrack(track: MusicTrack, onNavigateToPlayer: () -> Unit) {
        viewModelScope.launch {
            selectTrack(track)
            
            try {
                val audioUrl = track.audioUrl ?: ""
                
                if (audioUrl.isNotBlank()) {
                    GlobalPlayerManager.playTrack(audioUrl)
                    onNavigateToPlayer()
                } else {
                    _uiState.value = MusicUiState.Error("URL трека не найден")
                }
            } catch (e: Exception) {
                _uiState.value = MusicUiState.Error("Ошибка воспроизведения: ${e.message}")
            }
        }
    }

    fun clearCurrentTrack() {
        GlobalPlayerManager.stop()
        _currentTrack.value = null
    }

    fun toggleFavorite(trackId: String) {
        viewModelScope.launch {
            val track = _allTracks.value.find { it.id == trackId } ?: _currentTrack.value
            if (track != null) {
                if (repository.isFavorite(trackId)) {
                    repository.removeFromFavorites(trackId)
                    _favoriteTracks.value = _favoriteTracks.value - trackId
                } else {
                    repository.addToFavorites(track)
                    _favoriteTracks.value = _favoriteTracks.value + trackId
                }
            }
        }
    }

    fun isFavorite(trackId: String): Boolean {
        return repository.isFavorite(trackId)
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isPlayingFromFavorites.value = true  // Включаем режим воспроизведения из избранного
            val favorites = repository.getFavorites()
            _favoriteTracks.value = favorites.map { it.id }.toSet()
            _uiState.value = MusicUiState.Success(favorites)
        }
    }

    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
        GlobalPlayerManager.setRepeatMode(_isRepeatEnabled.value)
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    fun playNext() {
        val currentTracks = getCurrentPlaylist()  // Используем текущий плейлист
        val currentTrackIndex = currentTracks.indexOfFirst { it.id == _currentTrack.value?.id }
        
        if (currentTrackIndex != -1 && currentTracks.isNotEmpty()) {
            val nextTrack = if (_isShuffleEnabled.value) {
                currentTracks.filter { it.id != _currentTrack.value?.id }.randomOrNull()
            } else if (_isRepeatEnabled.value && currentTrackIndex == currentTracks.size - 1) {
                currentTracks.first()
            } else if (currentTrackIndex < currentTracks.size - 1) {
                currentTracks[currentTrackIndex + 1]
            } else {
                null
            }
            
            nextTrack?.let { track ->
                selectTrack(track)
                track.audioUrl?.let { url ->
                    GlobalPlayerManager.playTrack(url)
                }
            }
        }
    }

    fun playPrevious() {
        val currentTracks = getCurrentPlaylist()  // Используем текущий плейлист
        val currentTrackIndex = currentTracks.indexOfFirst { it.id == _currentTrack.value?.id }
        
        if (currentTrackIndex != -1 && currentTracks.isNotEmpty()) {
            val previousTrack = if (_isShuffleEnabled.value) {
                currentTracks.filter { it.id != _currentTrack.value?.id }.randomOrNull()
            } else if (_isRepeatEnabled.value && currentTrackIndex == 0) {
                currentTracks.last()
            } else if (currentTrackIndex > 0) {
                currentTracks[currentTrackIndex - 1]
            } else {
                null
            }
            
            previousTrack?.let { track ->
                selectTrack(track)
                track.audioUrl?.let { url ->
                    GlobalPlayerManager.playTrack(url)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        GlobalPlayerManager.release()
    }
}
