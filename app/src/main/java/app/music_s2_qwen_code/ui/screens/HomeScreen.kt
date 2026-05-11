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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.music_s2_qwen_code.R
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.ui.theme.*
import app.music_s2_qwen_code.viewmodel.HomeTab

@Composable
fun HomeScreen(
    songs: List<Song>,
    selectedTab: HomeTab,
    searchQuery: String,
    isLoading: Boolean,
    onTabSelected: (HomeTab) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onSongClick: (Song, Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
                                count = 0,
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
                        onClick = { onSongClick(song, songs.indexOf(song)) }
                    )
                }
            }
        }
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Gray.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryGradientStart)
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
fun SongItem(
    song: Song,
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
        
        Text(
            text = song.formatDuration(),
            color = SecondaryText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
