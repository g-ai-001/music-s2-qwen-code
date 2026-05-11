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

### v0.2.2 (当前版本)
- 修复GitHub Actions构建失败问题
- 添加缺失的AccentPink颜色定义
- 优化项目资源配置
- 版本号更新至0.2.2

### v0.2.1
- 修复GitHub Actions构建失败问题
- 修复gradlew文件权限问题
- 版本号更新至0.2.1

### v0.2.0
- 实现Room数据库，支持本地数据持久化
- 完善歌单管理功能（创建歌单、管理歌曲）
- 实现收藏功能（添加/移除收藏歌曲）
- 更新UI支持歌单和收藏显示
- 优化播放器界面，支持收藏状态显示
- 版本号更新至0.2.0

### v0.1.3
- 修复GitHub Actions构建失败问题
- 补充缺失的应用图标资源
- 修复ic_launcher_foreground资源引用问题

### v0.1.2
- 代码审查和优化
- 优化日志系统实现（添加日志轮转、线程安全）
- 优化ViewModel状态管理（统一UiState）
- 添加详细日志记录
- 代码格式化和规范化

### v0.1.1
- 修复GitHub Actions构建失败问题
- 补充缺失的gradle-wrapper.jar文件
- 优化CI/CD流程

### v0.1.0
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
