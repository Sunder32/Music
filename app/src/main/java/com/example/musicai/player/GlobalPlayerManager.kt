package com.example.musicai.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Глобальный менеджер аудиоплеера
 * Гарантирует что играет только один трек в любой момент времени
 */
object GlobalPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private var updateJob: kotlinx.coroutines.Job? = null
    private var onTrackEndedCallback: (() -> Unit)? = null
    
    fun initialize(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_READY -> {
                                _duration.value = this@apply.duration
                            }
                            Player.STATE_ENDED -> {
                                _isPlaying.value = false
                                onTrackEndedCallback?.invoke()
                            }
                        }
                    }
                    
                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                    }
                })
                repeatMode = Player.REPEAT_MODE_OFF
            }
        }
    }
    
    fun setOnTrackEndedCallback(callback: () -> Unit) {
        onTrackEndedCallback = callback
    }
    
    fun setRepeatMode(enabled: Boolean) {
        exoPlayer?.repeatMode = if (enabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }
    
    fun playTrack(url: String) {
        if (url.isBlank()) {
            return
        }
        exoPlayer?.let { player ->
            try {
                player.stop()
                player.clearMediaItems()
                player.setMediaItem(MediaItem.fromUri(url.toUri()))
                player.prepare()
                player.play()
                _isPlaying.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _isPlaying.value = false
            }
        }
    }
    
    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }
    
    fun stop() {
        exoPlayer?.stop()
        _isPlaying.value = false
        _currentPosition.value = 0L
    }
    
    fun release() {
        updateJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
    }
    
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }
    
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    
    fun getDuration(): Long = exoPlayer?.duration ?: 0L
    
    fun isPlayerPlaying(): Boolean = exoPlayer?.isPlaying ?: false
}
