package app.music_s2_qwen_code.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 收藏数据实体
 */
@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey
    val songId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
