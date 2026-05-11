package app.music_s2_qwen_code

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.music_s2_qwen_code.data.repository.DataRepository
import app.music_s2_qwen_code.service.MusicPlaybackService
import app.music_s2_qwen_code.ui.components.BottomNavigationBar
import app.music_s2_qwen_code.ui.components.BottomNavTab
import app.music_s2_qwen_code.ui.components.MiniPlayer
import app.music_s2_qwen_code.ui.screens.HomeScreen
import app.music_s2_qwen_code.ui.screens.MeScreen
import app.music_s2_qwen_code.ui.screens.PlayerScreen
import app.music_s2_qwen_code.ui.theme.MusicS2QwenCodeTheme
import app.music_s2_qwen_code.utils.Logger
import app.music_s2_qwen_code.viewmodel.LibraryViewModel
import app.music_s2_qwen_code.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"
    
    private lateinit var dataRepository: DataRepository
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var playerViewModel: PlayerViewModel

    private var playbackService: MusicPlaybackService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Logger.d(tag, "服务已连接")
            val binder = service as MusicPlaybackService.LocalBinder
            playbackService = binder.getService()
            playerViewModel.setPlaybackService(binder.getService())
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Logger.d(tag, "服务已断开")
            isServiceBound = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Logger.i(tag, "所有权限已授予")
            loadSongs()
        } else {
            Logger.w(tag, "部分权限被拒绝")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(tag, "onCreate")
        Logger.init(this)

        // 初始化数据仓库和ViewModel
        dataRepository = (application as MusicApplication).dataRepository
        
        libraryViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LibraryViewModel(dataRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[LibraryViewModel::class.java]

        playerViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PlayerViewModel(dataRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[PlayerViewModel::class.java]

        setContent {
            MusicS2QwenCodeTheme {
                MainScreen(
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel,
                    onRefresh = { loadSongs() },
                    onSongClick = { songs, index ->
                        playerViewModel.setPlaylist(songs, index)
                        playerViewModel.play()
                    }
                )
            }
        }

        checkPermissionsAndLoadSongs()
        bindPlaybackService()
    }

    private fun checkPermissionsAndLoadSongs() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            loadSongs()
        }
    }

    private fun loadSongs() {
        libraryViewModel.loadSongs(this)
    }

    private fun bindPlaybackService() {
        Intent(this, MusicPlaybackService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(tag, "onDestroy")
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}

@Composable
fun MainScreen(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onRefresh: () -> Unit,
    onSongClick: (List<app.music_s2_qwen_code.data.model.Song>, Int) -> Unit
) {
    val uiState by libraryViewModel.uiState.collectAsState()
    val playerUiState by playerViewModel.uiState.collectAsState()

    var selectedBottomTab by remember { mutableStateOf(BottomNavTab.HOME) }
    var showPlayerScreen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedBottomTab) {
                    BottomNavTab.HOME -> HomeScreen(
                        songs = uiState.allSongs,
                        playlists = uiState.playlists,
                        favoriteSongs = uiState.favoriteSongs,
                        selectedTab = uiState.selectedTab,
                        searchQuery = uiState.searchQuery,
                        isLoading = uiState.isLoading,
                        onTabSelected = { libraryViewModel.selectTab(it) },
                        onSearchQueryChange = { libraryViewModel.updateSearchQuery(it) },
                        onRefresh = onRefresh,
                        onCreatePlaylist = { libraryViewModel.createPlaylist(it) },
                        onToggleFavorite = { libraryViewModel.toggleFavorite(it) },
                        onSongClick = { song, index ->
                            onSongClick(uiState.allSongs, index)
                            showPlayerScreen = true
                        }
                    )
                    BottomNavTab.ME -> MeScreen(
                        songs = uiState.allSongs,
                        favoriteSongs = uiState.favoriteSongs,
                        playlists = uiState.playlists,
                        onToggleFavorite = { libraryViewModel.toggleFavorite(it) },
                        onSongClick = { song, index ->
                            onSongClick(uiState.allSongs, index)
                            showPlayerScreen = true
                        }
                    )
                }
            }

            if (playerUiState.currentSong != null) {
                MiniPlayer(
                    currentSong = playerUiState.currentSong,
                    isPlaying = playerUiState.isPlaying,
                    onPlayPause = {
                        if (playerUiState.isPlaying) {
                            playerViewModel.pause()
                        } else {
                            playerViewModel.play()
                        }
                    },
                    onNext = { playerViewModel.playNext() },
                    onClick = { showPlayerScreen = true }
                )
            }

            BottomNavigationBar(
                selectedTab = selectedBottomTab,
                onTabSelected = { selectedBottomTab = it }
            )
        }

        if (showPlayerScreen && playerUiState.currentSong != null) {
            PlayerScreen(
                currentSong = playerUiState.currentSong,
                isPlaying = playerUiState.isPlaying,
                currentPosition = playerUiState.currentPosition,
                duration = playerUiState.duration,
                playerMode = playerUiState.playerMode,
                isFavorite = playerUiState.isFavorite,
                onPlayPause = {
                    if (playerUiState.isPlaying) {
                        playerViewModel.pause()
                    } else {
                        playerViewModel.play()
                    }
                },
                onNext = { playerViewModel.playNext() },
                onPrevious = { playerViewModel.playPrevious() },
                onSeekTo = { playerViewModel.seekTo(it) },
                onToggleMode = { playerViewModel.togglePlayerMode() },
                onToggleFavorite = { playerViewModel.toggleFavorite() },
                onDismiss = { showPlayerScreen = false }
            )
        }
    }
}
