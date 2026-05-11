package app.music_s2_qwen_code.data.dao

import androidx.room.*
import app.music_s2_qwen_code.data.model.Favorite
import app.music_s2_qwen_code.data.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * 收藏数据访问对象
 */
@Dao
interface FavoriteDao {
    @Query("SELECT s.* FROM songs s " +
           "INNER JOIN favorites f ON s.id = f.songId " +
           "ORDER BY f.addedAt DESC")
    fun getFavoriteSongs(): Flow<List<Song>>

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun removeFavoriteBySongId(songId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavorite(songId: Long): Boolean

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getFavoriteCount(): Int
}
