package app.music_s2_qwen_code.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.data.repository.MusicRepository
import app.music_s2_qwen_code.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val tag = "LibraryViewModel"
    
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    // 兼容性属性
    val allSongs: StateFlow<List<Song>> get() = _uiState.value.allSongs.let { 
        MutableStateFlow(it).asStateFlow() 
    }
    val isLoading: StateFlow<Boolean> get() = _uiState.value.isLoading.let { 
        MutableStateFlow(it).asStateFlow() 
    }
    val selectedTab: StateFlow<HomeTab> get() = _uiState.value.selectedTab.let { 
        MutableStateFlow(it).asStateFlow() 
    }
    val searchQuery: StateFlow<String> get() = _uiState.value.searchQuery.let { 
        MutableStateFlow(it).asStateFlow() 
    }

    fun loadSongs(context: Context) {
        Logger.d(tag, "开始加载本地音乐")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val repository = MusicRepository(context)
            val songs = repository.getAllSongs()
            _uiState.value = _uiState.value.copy(
                allSongs = songs,
                isLoading = false
            )
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
        return if (query.isEmpty()) {
            _uiState.value.allSongs
        } else {
            _uiState.value.allSongs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true)
            }
        }
    }
}

data class LibraryUiState(
    val allSongs: List<Song> = emptyList(),
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
