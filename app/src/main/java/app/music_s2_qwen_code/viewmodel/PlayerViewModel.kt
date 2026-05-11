package app.music_s2_qwen_code.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.service.MusicPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    private var playbackService: MusicPlaybackService? = null
    private var progressUpdateJob: Job? = null
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _playerMode = MutableStateFlow(PlayerMode.COVER)
    val playerMode: StateFlow<PlayerMode> = _playerMode.asStateFlow()
    
    fun setPlaybackService(service: MusicPlaybackService) {
        playbackService = service
        updatePlayerState()
    }
    
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        playbackService?.setPlaylist(songs, startIndex)
        updatePlayerState()
    }
    
    fun play() {
        playbackService?.play()
        updatePlayerState()
        startProgressUpdate()
    }
    
    fun pause() {
        playbackService?.pause()
        updatePlayerState()
        stopProgressUpdate()
    }
    
    fun seekTo(positionMs: Long) {
        playbackService?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }
    
    fun playNext() {
        playbackService?.playNext()
        updatePlayerState()
    }
    
    fun playPrevious() {
        playbackService?.playPrevious()
        updatePlayerState()
    }
    
    fun togglePlayerMode() {
        _playerMode.value = when (_playerMode.value) {
            PlayerMode.COVER -> PlayerMode.LYRICS
            PlayerMode.LYRICS -> PlayerMode.COVER
        }
    }
    
    private fun updatePlayerState() {
        _currentSong.value = playbackService?.getCurrentSong()
        _isPlaying.value = playbackService?.isPlaying() ?: false
        _currentPosition.value = playbackService?.getCurrentPosition() ?: 0L
        _duration.value = playbackService?.getDuration() ?: 0L
    }
    
    private fun startProgressUpdate() {
        stopProgressUpdate()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                updatePlayerState()
                delay(100)
            }
        }
    }
    
    private fun stopProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }
    
    override fun onCleared() {
        super.onCleared()
        stopProgressUpdate()
    }
}

enum class PlayerMode {
    COVER,
    LYRICS
}
