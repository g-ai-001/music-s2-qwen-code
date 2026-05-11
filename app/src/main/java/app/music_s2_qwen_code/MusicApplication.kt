package app.music_s2_qwen_code

import android.app.Application
import app.music_s2_qwen_code.data.repository.DataRepository
import app.music_s2_qwen_code.utils.Logger

class MusicApplication : Application() {
    lateinit var dataRepository: DataRepository
        private set

    override fun onCreate() {
        super.onCreate()
        Logger.init(this)
        Logger.i("MusicApplication", "应用启动")
        
        dataRepository = DataRepository(this)
        Logger.i("MusicApplication", "数据仓库初始化完成")
    }
}
