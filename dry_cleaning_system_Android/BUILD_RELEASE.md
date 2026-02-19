# APK 打包发布指南

## 1. 构建变体说明

### Debug 版本
- 用于开发和测试
- 包含调试信息
- 未启用代码混淆
- 包名：`com.chaser.drycleaningsystem`
- 版本后缀：无

### Release 版本
- 用于正式发布
- 启用代码混淆 (ProGuard)
- 启用资源压缩
- 已优化性能
- 包名：`com.chaser.drycleaningsystem`

## 2. 构建命令

### 使用 Android Studio
1. 点击菜单 `Build` > `Generate Signed Bundle / APK`
2. 选择 `APK` 选项
3. 选择 `release` 构建变体
4. 输入密钥库信息（首次创建需生成新密钥）
5. 点击 `Finish`

### 使用命令行
```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 清理并构建 Release APK
./gradlew clean assembleRelease
```

## 3. 密钥库生成（首次发布）

```bash
keytool -genkey -v -keystore dry-cleaning-system.keystore -alias dry-cleaning -keyalg RSA -keysize 2048 -validity 10000
```

**重要：** 请安全保存密钥库文件和密码，丢失后将无法更新应用。

## 4. APK 输出位置

构建完成后，APK 文件位于：
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 5. 签名 Release APK

如果使用命令行构建，需要手动签名：

```bash
# 使用 jarsigner 签名
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore dry-cleaning-system.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  dry-cleaning

# 使用 zipalign 优化
zipalign -v 4 \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  app-release.apk

# 验证签名
jarsigner -verify -verbose -certs app-release.apk
```

## 6. 版本管理

在 `app/build.gradle.kts` 中修改版本号：

```kotlin
defaultConfig {
    versionCode = 1      // 每次发布递增
    versionName = "1.0"  // 显示给用户的版本号
}
```

## 7. 发布前检查清单

- [ ] 所有功能测试通过
- [ ] 数据库测试通过
- [ ] 在目标平板设备上测试
- [ ] 检查所有 UI 元素正常显示
- [ ] 验证横屏模式正常
- [ ] 测试充值计算逻辑
- [ ] 测试统计报表数据准确性
- [ ] 确认无崩溃和 ANR
- [ ] 准备好应用图标和截图

## 8. 安装 APK 到平板

```bash
# 通过 ADB 安装
adb install app-release.apk

# 如果已安装，覆盖安装
adb install -r app-release.apk
```

## 9. 应用信息

- **应用名称**: 干洗店管理系统
- **包名**: com.chaser.drycleaningsystem
- **最低 Android 版本**: Android 5.0 (API 24)
- **目标设备**: Android 平板（横屏使用）
- **应用类型**: 单机版业务管理系统
