package app.music_s2_qwen_code.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock

object Logger {
    private const val LOG_FILE_NAME = "music_player_log.txt"
    private const val MAX_LOG_FILE_SIZE = 5 * 1024 * 1024 // 5MB
    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val lock = ReentrantLock()

    fun init(context: Context) {
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null) {
            logFile = File(externalFilesDir, LOG_FILE_NAME)
            rotateLogFileIfNeeded()
        }
        d("Logger", "日志系统初始化完成")
    }

    private fun rotateLogFileIfNeeded() {
        logFile?.let { file ->
            if (file.exists() && file.length() > MAX_LOG_FILE_SIZE) {
                val backupFile = File(file.parent, "music_player_log_old.txt")
                if (backupFile.exists()) {
                    backupFile.delete()
                }
                file.renameTo(backupFile)
            }
        }
    }

    fun d(tag: String, message: String) {
        log(LogLevel.DEBUG, tag, message)
    }

    fun i(tag: String, message: String) {
        log(LogLevel.INFO, tag, message)
    }

    fun w(tag: String, message: String) {
        log(LogLevel.WARN, tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val fullMessage = if (throwable != null) {
            "$message\n${android.util.Log.getStackTraceString(throwable)}"
        } else {
            message
        }
        log(LogLevel.ERROR, tag, fullMessage)
    }

    private fun log(level: LogLevel, tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] [${level.name}] [$tag] $message\n"

        android.util.Log.println(
            level.priority,
            tag,
            message
        )

        logFile?.let { file ->
            lock.lock()
            try {
                FileWriter(file, true).use { writer ->
                    writer.append(logMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("Logger", "写入日志文件失败", e)
            } finally {
                lock.unlock()
            }
        }
    }
}

enum class LogLevel(val priority: Int) {
    DEBUG(android.util.Log.DEBUG),
    INFO(android.util.Log.INFO),
    WARN(android.util.Log.WARN),
    ERROR(android.util.Log.ERROR)
}
