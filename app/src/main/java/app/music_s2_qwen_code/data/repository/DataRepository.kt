package app.music_s2_qwen_code.data.repository

import android.content.Context
import app.music_s2_qwen_code.data.dao.FavoriteDao
import app.music_s2_qwen_code.data.dao.PlaylistDao
import app.music_s2_qwen_code.data.dao.SongDao
import app.music_s2_qwen_code.data.database.AppDatabase
import app.music_s2_qwen_code.data.model.Favorite
import app.music_s2_qwen_code.data.model.Playlist
import app.music_s2_qwen_code.data.model.PlaylistSongMap
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 数据仓库 - 管理歌单和收藏功能
 */
class DataRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val songDao: SongDao = db.songDao()
    private val playlistDao: PlaylistDao = db.playlistDao()
    private val favoriteDao: FavoriteDao = db.favoriteDao()

    // 歌单相关
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    suspend fun createPlaylist(name: String, coverUri: String? = null): Long {
        return withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "创建歌单: $name")
            val playlist = Playlist(name = name, coverUri = coverUri)
            val id = playlistDao.insertPlaylist(playlist)
            id
        }
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "删除歌单: ${playlist.name}")
            playlistDao.clearPlaylist(playlist.id)
            playlistDao.deletePlaylist(playlist)
        }
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsInPlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "添加歌曲 $songId 到歌单 $playlistId")
            val count = playlistDao.getSongCountInPlaylist(playlistId)
            val map = PlaylistSongMap(
                playlistId = playlistId,
                songId = songId,
                order = count
            )
            playlistDao.addSongToPlaylist(map)
        }
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "从歌单 $playlistId 移除歌曲 $songId")
            playlistDao.removeSongFromPlaylist(playlistId, songId)
        }
    }

    suspend fun isSongInPlaylist(playlistId: Long, songId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            playlistDao.isSongInPlaylist(playlistId, songId)
        }
    }

    // 收藏相关
    val favoriteSongs: Flow<List<Song>> = favoriteDao.getFavoriteSongs()

    suspend fun addFavorite(songId: Long) {
        withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "添加收藏: $songId")
            val favorite = Favorite(songId = songId)
            favoriteDao.addFavorite(favorite)
        }
    }

    suspend fun removeFavorite(songId: Long) {
        withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "移除收藏: $songId")
            favoriteDao.removeFavoriteBySongId(songId)
        }
    }

    suspend fun isFavorite(songId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteDao.isFavorite(songId)
        }
    }

    suspend fun toggleFavorite(songId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val isFav = favoriteDao.isFavorite(songId)
            if (isFav) {
                favoriteDao.removeFavoriteBySongId(songId)
                false
            } else {
                favoriteDao.addFavorite(Favorite(songId = songId))
                true
            }
        }
    }

    // 歌曲缓存相关
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()

    suspend fun cacheSongs(songs: List<Song>) {
        withContext(Dispatchers.IO) {
            Logger.d("DataRepository", "缓存 ${songs.size} 首歌曲到数据库")
            val dbSongs = songs.map { song ->
                song.copy(albumArtUriString = song.albumArtUri?.toString())
            }
            songDao.insertSongs(dbSongs)
        }
    }

    suspend fun getSongById(songId: Long): Song? {
        return withContext(Dispatchers.IO) {
            songDao.getSongById(songId)
        }
    }

    fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query)
    }

    suspend fun initDefaultPlaylists() {
        withContext(Dispatchers.IO) {
            val playlists = playlistDao.getAllPlaylists()
            // 检查是否已有歌单，没有则创建默认歌单
            // 这里不需要实际检查，因为Flow不能直接在suspend中获取
            // 默认歌单会在UI层动态处理
        }
    }
}
