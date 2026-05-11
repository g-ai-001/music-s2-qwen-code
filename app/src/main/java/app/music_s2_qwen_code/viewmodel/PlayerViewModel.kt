package app.music_s2_qwen_code.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.service.MusicPlaybackService
import app.music_s2_qwen_code.utils.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    private val tag = "PlayerViewModel"
    
    private var playbackService: MusicPlaybackService? = null
    private var progressUpdateJob: Job? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun setPlaybackService(service: MusicPlaybackService) {
        Logger.d(tag, "设置播放服务")
        playbackService = service
        updatePlayerState()
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        Logger.d(tag, "设置播放列表，${songs.size}首歌")
        playbackService?.setPlaylist(songs, startIndex)
        updatePlayerState()
    }

    fun play() {
        Logger.d(tag, "播放")
        playbackService?.play()
        updatePlayerState()
        startProgressUpdate()
    }

    fun pause() {
        Logger.d(tag, "暂停")
        playbackService?.pause()
        updatePlayerState()
        stopProgressUpdate()
    }

    fun seekTo(positionMs: Long) {
        playbackService?.seekTo(positionMs)
        _uiState.value = _uiState.value.copy(currentPosition = positionMs)
    }

    fun playNext() {
        Logger.d(tag, "下一首")
        playbackService?.playNext()
        updatePlayerState()
    }

    fun playPrevious() {
        Logger.d(tag, "上一首")
        playbackService?.playPrevious()
        updatePlayerState()
    }

    fun togglePlayerMode() {
        _uiState.value = _uiState.value.copy(
            playerMode = when (_uiState.value.playerMode) {
                PlayerMode.COVER -> PlayerMode.LYRICS
                PlayerMode.LYRICS -> PlayerMode.COVER
            }
        )
    }

    private fun updatePlayerState() {
        _uiState.value = _uiState.value.copy(
            currentSong = playbackService?.getCurrentSong(),
            isPlaying = playbackService?.isPlaying() ?: false,
            currentPosition = playbackService?.getCurrentPosition() ?: 0L,
            duration = playbackService?.getDuration() ?: 0L
        )
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
        Logger.d(tag, "ViewModel 销毁")
        super.onCleared()
        stopProgressUpdate()
    }
}

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playerMode: PlayerMode = PlayerMode.COVER
)

enum class PlayerMode {
    COVER,
    LYRICS
}
