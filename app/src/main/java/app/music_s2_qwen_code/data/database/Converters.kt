package app.music_s2_qwen_code.data.database

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Room类型转换器
 */
class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}
