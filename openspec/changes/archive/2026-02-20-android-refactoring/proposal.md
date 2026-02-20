## Why

由于 HarmonyOS 4.1 SDK 功能限制，平板设备无法正常运行应用。需要将现有 Java 后端项目改造成 Android 单机应用，以便在普通 Android 平板上运行。

## What Changes

- 创建新的 Android 项目 `dry_cleaning_system_Android`
- 将 Spring Boot 后端逻辑移植到 Android 本地代码
- 使用 SQLite 替代原有数据库
- 实现离线数据存储和管理
- **BREAKING**: 不再依赖后端服务器，所有数据本地存储

## Capabilities

### New Capabilities
- `android-customer-management`: Android 客户管理模块，支持客户信息的增删改查
- `android-order-management`: Android 订单管理模块，支持订单创建、状态更新和查询
- `android-prepaid-system`: Android 会员充值模块，支持充值和余额管理
- `android-statistics`: Android 统计报表模块，支持日报月报查询
- `android-local-database`: Android 本地数据库模块，使用 SQLite 存储数据

### Modified Capabilities
<!-- 无现有能力需要修改，这是全新的 Android 应用 -->

## Impact

- **新项目目录**: `dry_cleaning_system_Android/`
- **新技术栈**: Android SDK + SQLite + Room (可选)
- **数据存储**: 从服务器数据库改为本地 SQLite
- **目标设备**: Android 平板（Android 5.0+）
- **开发工具**: Android Studio
