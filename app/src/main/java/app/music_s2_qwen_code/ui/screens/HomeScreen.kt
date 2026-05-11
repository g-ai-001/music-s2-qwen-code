package app.music_s2_qwen_code.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.music_s2_qwen_code.data.model.Playlist
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.ui.theme.*
import app.music_s2_qwen_code.viewmodel.HomeTab

@Composable
fun HomeScreen(
    songs: List<Song>,
    playlists: List<Playlist>,
    favoriteSongs: List<Song>,
    selectedTab: HomeTab,
    searchQuery: String,
    isLoading: Boolean,
    onTabSelected: (HomeTab) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onSongClick: (Song, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("搜索本地音乐...", color = SecondaryText) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索", tint = SecondaryText)
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "刷新",
                    tint = Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = AccentGreen
                )
            }
        ) {
            HomeTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = when (tab) {
                                HomeTab.RECOMMENDED -> "推荐"
                                HomeTab.PLAYLISTS -> "歌单"
                                HomeTab.ARTISTS -> "歌手"
                                HomeTab.ALBUMS -> "专辑"
                            },
                            color = if (selectedTab == tab) AccentGreen else SecondaryText,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentGreen)
            }
        } else {
            when (selectedTab) {
                HomeTab.RECOMMENDED -> RecommendedContent(
                    songs = songs,
                    favoriteSongs = favoriteSongs,
                    onToggleFavorite = onToggleFavorite,
                    onSongClick = onSongClick
                )
                HomeTab.PLAYLISTS -> PlaylistsContent(
                    playlists = playlists,
                    favoriteSongs = favoriteSongs,
                    songs = songs,
                    onCreatePlaylist = { showCreatePlaylistDialog = true },
                    onToggleFavorite = onToggleFavorite,
                    onSongClick = onSongClick
                )
                HomeTab.ARTISTS -> ArtistsContent(songs = songs)
                HomeTab.ALBUMS -> AlbumsContent(songs = songs)
            }
        }
    }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { name ->
                onCreatePlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }
}

@Composable
fun RecommendedContent(
    songs: List<Song>,
    favoriteSongs: List<Song>,
    onToggleFavorite: (Long) -> Unit,
    onSongClick: (Song, Int) -> Unit
) {
    val favoriteSongIds = remember(favoriteSongs) {
        favoriteSongs.map { it.id }.toSet()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "最近播放",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(songs.take(10)) { song ->
                        RecentPlayItem(song = song)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "本地歌单",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlaylistCard(
                        title = "所有歌曲",
                        count = songs.size,
                        modifier = Modifier.weight(1f)
                    )
                    PlaylistCard(
                        title = "我的收藏",
                        count = favoriteSongs.size,
                        isFavorite = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "歌曲列表",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(songs) { song ->
            SongItem(
                song = song,
                isFavorite = favoriteSongIds.contains(song.id),
                onToggleFavorite = { onToggleFavorite(song.id) },
                onClick = { onSongClick(song, songs.indexOf(song)) }
            )
        }
    }
}

@Composable
fun PlaylistsContent(
    playlists: List<Playlist>,
    favoriteSongs: List<Song>,
    songs: List<Song>,
    onCreatePlaylist: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onSongClick: (Song, Int) -> Unit
) {
    val favoriteSongIds = remember(favoriteSongs) {
        favoriteSongs.map { it.id }.toSet()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的歌单",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onCreatePlaylist) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "创建歌单",
                        tint = AccentGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlaylistCard(
                    title = "我的收藏",
                    count = favoriteSongs.size,
                    isFavorite = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        items(playlists) { playlist ->
            PlaylistItem(playlist = playlist)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "收藏歌曲",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(favoriteSongs) { song ->
            SongItem(
                song = song,
                isFavorite = true,
                onToggleFavorite = { onToggleFavorite(song.id) },
                onClick = { onSongClick(song, songs.indexOf(song)) }
            )
        }
    }
}

@Composable
fun ArtistsContent(songs: List<Song>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "歌手功能开发中...",
            color = SecondaryText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AlbumsContent(songs: List<Song>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "专辑功能开发中...",
            color = SecondaryText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RecentPlayItem(
    song: Song,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            color = Color.White,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PlaylistCard(
    title: String,
    count: Int,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFavorite) AccentGreen.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isFavorite) AccentPink else PrimaryGradientStart)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "$count 首",
            color = SecondaryText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryGradientStart)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Icon(
            Icons.Default.MoreVert,
            contentDescription = "更多",
            tint = SecondaryText
        )
    }
}

@Composable
fun SongItem(
    song: Song,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = song.artist,
                color = SecondaryText,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
        }

        IconButton(onClick = onToggleFavorite) {
            Icon(
                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                tint = if (isFavorite) AccentPink else SecondaryText
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = song.formatDuration(),
            color = SecondaryText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBackground)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "新建歌单",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请输入歌单名称", color = SecondaryText) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = SecondaryText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { if (playlistName.isNotBlank()) onCreate(playlistName) },
                        enabled = playlistName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                    ) {
                        Text("创建")
                    }
                }
            }
        }
    }
}
