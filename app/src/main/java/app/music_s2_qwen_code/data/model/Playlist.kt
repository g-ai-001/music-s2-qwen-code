package app.music_s2_qwen_code.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 歌单数据实体
 */
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val coverUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
