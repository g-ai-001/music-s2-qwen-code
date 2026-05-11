package app.music_s2_qwen_code.data.model

import androidx.room.Entity
import androidx.room.Index

/**
 * 歌单与歌曲的映射表（多对多关系）
 */
@Entity(
    tableName = "playlist_song_map",
    primaryKeys = ["playlistId", "songId"],
    indices = [Index("playlistId"), Index("songId")]
)
data class PlaylistSongMap(
    val playlistId: Long,
    val songId: Long,
    val order: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)
