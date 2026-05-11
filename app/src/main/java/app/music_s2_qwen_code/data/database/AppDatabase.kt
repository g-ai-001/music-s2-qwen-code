package app.music_s2_qwen_code.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.music_s2_qwen_code.data.dao.FavoriteDao
import app.music_s2_qwen_code.data.dao.PlaylistDao
import app.music_s2_qwen_code.data.dao.SongDao
import app.music_s2_qwen_code.data.model.Favorite
import app.music_s2_qwen_code.data.model.Playlist
import app.music_s2_qwen_code.data.model.PlaylistSongMap
import app.music_s2_qwen_code.data.model.Song

/**
 * 应用数据库
 */
@Database(
    entities = [
        Song::class,
        Playlist::class,
        PlaylistSongMap::class,
        Favorite::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_player_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
