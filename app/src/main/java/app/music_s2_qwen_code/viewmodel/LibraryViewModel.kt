package app.music_s2_qwen_code.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(HomeTab.RECOMMENDED)
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    fun loadSongs(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val repository = MusicRepository(context)
            _allSongs.value = repository.getAllSongs()
            _isLoading.value = false
        }
    }
    
    fun selectTab(tab: HomeTab) {
        _selectedTab.value = tab
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getFilteredSongs(): List<Song> {
        val query = _searchQuery.value
        return if (query.isEmpty()) {
            _allSongs.value
        } else {
            _allSongs.value.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true)
            }
        }
    }
}

enum class HomeTab {
    RECOMMENDED,
    PLAYLISTS,
    ARTISTS,
    ALBUMS
}
