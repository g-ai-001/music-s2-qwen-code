package app.music_s2_qwen_code

import android.app.Application
import app.music_s2_qwen_code.utils.Logger

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.init(this)
        Logger.i("MusicApplication", "应用启动")
    }
}
