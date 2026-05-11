package app.music_s2_qwen_code.utils

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    private const val LOG_FILE_NAME = "music_player_log.txt"
    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun init(context: Context) {
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null) {
            logFile = File(externalFilesDir, LOG_FILE_NAME)
        }
        d("Logger", "日志系统初始化完成")
    }

    fun d(tag: String, message: String) {
        log("DEBUG", tag, message)
    }

    fun i(tag: String, message: String) {
        log("INFO", tag, message)
    }

    fun w(tag: String, message: String) {
        log("WARN", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val fullMessage = if (throwable != null) {
            "$message\n${android.util.Log.getStackTraceString(throwable)}"
        } else {
            message
        }
        log("ERROR", tag, fullMessage)
    }

    private fun log(level: String, tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] [$level] [$tag] $message\n"
        
        android.util.Log.println(
            when (level) {
                "DEBUG" -> android.util.Log.DEBUG
                "INFO" -> android.util.Log.INFO
                "WARN" -> android.util.Log.WARN
                "ERROR" -> android.util.Log.ERROR
                else -> android.util.Log.INFO
            },
            tag,
            message
        )
        
        logFile?.let { file ->
            try {
                FileWriter(file, true).use { writer ->
                    writer.append(logMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("Logger", "写入日志文件失败", e)
            }
        }
    }
}
