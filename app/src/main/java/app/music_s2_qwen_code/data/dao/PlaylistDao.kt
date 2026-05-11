package app.music_s2_qwen_code.data.dao

import androidx.room.*
import app.music_s2_qwen_code.data.model.Playlist
import app.music_s2_qwen_code.data.model.PlaylistSongMap
import app.music_s2_qwen_code.data.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * 歌单数据访问对象
 */
@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): Playlist?

    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT s.* FROM songs s " +
           "INNER JOIN playlist_song_map psm ON s.id = psm.songId " +
           "WHERE psm.playlistId = :playlistId " +
           "ORDER BY psm.order ASC, psm.addedAt DESC")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>

    @Query("SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = :playlistId")
    suspend fun getSongCountInPlaylist(playlistId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(map: PlaylistSongMap)

    @Query("DELETE FROM playlist_song_map WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_song_map WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_song_map WHERE playlistId = :playlistId AND songId = :songId)")
    suspend fun isSongInPlaylist(playlistId: Long, songId: Long): Boolean
}
