package app.music_s2_qwen_code.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import app.music_s2_qwen_code.MainActivity
import app.music_s2_qwen_code.R
import app.music_s2_qwen_code.data.model.Song
import app.music_s2_qwen_code.utils.Logger

class MusicPlaybackService : MediaSessionService() {
    private val tag = "MusicPlaybackService"
    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer
    private val binder = LocalBinder()
    private val notificationId = 1
    private val channelId = "music_playback_channel"
    
    private var currentPlaylist: List<Song> = emptyList()
    private var currentIndex: Int = 0
    
    inner class LocalBinder : Binder() {
        fun getService(): MusicPlaybackService = this@MusicPlaybackService
    }
    
    override fun onCreate() {
        super.onCreate()
        Logger.d(tag, "音乐播放服务创建")
        
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
        
        createNotificationChannel()
        
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification()
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateNotification()
            }
        })
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
    
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        Logger.d(tag, "设置播放列表，共${songs.size}首，从第${startIndex}首开始")
        currentPlaylist = songs
        currentIndex = startIndex
        
        player.clearMediaItems()
        songs.forEach { song ->
            val mediaItem = MediaItem.fromUri(song.path)
            player.addMediaItem(mediaItem)
        }
        player.prepare()
        if (songs.isNotEmpty()) {
            player.seekTo(startIndex, 0)
        }
    }
    
    fun play() {
        player.play()
        startForeground(notificationId, createNotification())
    }
    
    fun pause() {
        player.pause()
    }
    
    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }
    
    fun playNext() {
        if (currentIndex < currentPlaylist.size - 1) {
            currentIndex++
            player.seekToNext()
        }
    }
    
    fun playPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            player.seekToPrevious()
        }
    }
    
    fun getCurrentSong(): Song? {
        return if (currentPlaylist.isNotEmpty() && currentIndex < currentPlaylist.size) {
            currentPlaylist[currentIndex]
        } else {
            null
        }
    }
    
    fun isPlaying(): Boolean = player.isPlaying
    
    fun getCurrentPosition(): Long = player.currentPosition
    
    fun getDuration(): Long = player.duration
    
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "音乐播放",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "本地音乐播放器通知"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val currentSong = getCurrentSong()
        val contentTitle = currentSong?.title ?: "无播放内容"
        val contentText = currentSong?.artist ?: ""
        
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, createNotification())
    }
    
    override fun onDestroy() {
        Logger.d(tag, "音乐播放服务销毁")
        player.release()
        mediaSession.release()
        super.onDestroy()
    }
}
