package app.music_s2_qwen_code.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 歌曲数据实体
 */
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumArtUriString: String? = null,
    val scannedAt: Long = System.currentTimeMillis()
) {
    val albumArtUri: Uri?
        get() = albumArtUriString?.let { Uri.parse(it) }

    fun formatDuration(): String {
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
