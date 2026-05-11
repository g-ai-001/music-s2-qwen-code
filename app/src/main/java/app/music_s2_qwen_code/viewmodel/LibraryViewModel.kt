package app.music_s2_qwen_code.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.music_s2_qwen_code.MusicApplication
import app.music_s2_qwen_code.data.model.Playlist
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.data.repository.DataRepository
import app.music_s2_qwen_code.data.repository.MusicRepository
import app.music_s2_qwen_code.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LibraryViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val tag = "LibraryViewModel"

    // 从DataRepository获取Flow
    val allPlaylists = dataRepository.allPlaylists
    val favoriteSongs = dataRepository.favoriteSongs

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        // 合并Flow更新UI状态
        viewModelScope.launch {
            combine(
                dataRepository.allSongs,
                dataRepository.allPlaylists,
                dataRepository.favoriteSongs
            ) { songs, playlists, favorites ->
                LibraryUiState(
                    allSongs = songs,
                    playlists = playlists,
                    favoriteSongs = favorites,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState.copy(
                    selectedTab = _uiState.value.selectedTab,
                    searchQuery = _uiState.value.searchQuery
                )
            }
        }
    }

    fun loadSongs(context: Context) {
        Logger.d(tag, "开始加载本地音乐")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val musicRepository = MusicRepository(context)
            val songs = musicRepository.getAllSongs()
            // 缓存到数据库
            dataRepository.cacheSongs(songs)
            Logger.d(tag, "加载完成，共${songs.size}首歌")
        }
    }

    fun selectTab(tab: HomeTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredSongs(): List<Song> {
        val query = _uiState.value.searchQuery
        val songs = _uiState.value.allSongs
        return if (query.isEmpty()) {
            songs
        } else {
            songs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true)
            }
        }
    }

    // 歌单操作
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            dataRepository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            dataRepository.deletePlaylist(playlist)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            dataRepository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            dataRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    // 收藏操作
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            dataRepository.toggleFavorite(songId)
        }
    }

    suspend fun isFavorite(songId: Long): Boolean {
        return dataRepository.isFavorite(songId)
    }
}

data class LibraryUiState(
    val allSongs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: HomeTab = HomeTab.RECOMMENDED,
    val searchQuery: String = ""
)

enum class HomeTab {
    RECOMMENDED,
    PLAYLISTS,
    ARTISTS,
    ALBUMS
}
