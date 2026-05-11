# music-s2-qwen-code

一个纯本地的Android音乐播放器，采用现代化的Material Design设计风格。

## 功能特性

- 🔍 **本地音乐扫描** - 自动扫描设备上的音频文件
- 🎵 **音乐播放** - 基于Media3(ExoPlayer)的高质量播放引擎
- 📱 **现代UI** - 使用Jetpack Compose构建的现代化界面
- 📄 **歌词显示** - 支持封面模式和歌词模式切换
- 📋 **播放列表** - 管理本地歌单和收藏
- 📝 **完整日志** - 内置日志系统，方便问题排查

## 版本历史

### v0.1.0 (当前版本)
- 初始化项目结构
- 实现基础UI框架（首页、播放页、我的页面）
- 集成Media3播放引擎
- 实现本地音乐扫描功能
- 添加日志系统
- 配置GitHub Actions CI/CD

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM
- **播放引擎**: Media3 (ExoPlayer)
- **最低SDK**: API 26 (Android 8.0)
- **目标SDK**: API 34 (Android 14)

## 构建说明

由于本地缺少构建环境，本项目通过GitHub Actions自动构建。

要在本地构建：
```bash
./gradlew assembleDebug
```

## 权限说明

- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_AUDIO` - 读取本地音乐文件
- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK` - 后台音乐播放
- `POST_NOTIFICATIONS` - 显示播放通知
- `WAKE_LOCK` - 保持播放时设备唤醒

## 项目结构

```
app/
├── src/main/java/app/music_s2_qwen_code/
│   ├── data/
│   │   ├── model/          # 数据模型
│   │   └── repository/     # 数据仓库
│   ├── service/            # 音乐播放服务
│   ├── ui/
│   │   ├── components/     # UI组件
│   │   ├── screens/        # 页面
│   │   └── theme/          # 主题配置
│   ├── utils/              # 工具类
│   ├── viewmodel/          # ViewModel
│   ├── MainActivity.kt
│   └── MusicApplication.kt
```

## 许可证

本项目仅供学习和个人使用。
