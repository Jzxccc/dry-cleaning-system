## Context

当前干洗店管理系统采用 Spring Boot + Thymeleaf 的 Web 架构，运行在浏览器中。为了扩展到华为设备用户，需要开发 HarmonyOS 原生应用。

**现有技术栈:**
- 后端：Spring Boot 2.7.0 + MyBatis-Plus + SQLite
- 前端：Thymeleaf + Bootstrap + 原生 JavaScript
- API: RESTful API

**目标技术栈:**
- HarmonyOS API Version: 10 (API 10)
- 语言：ArkTS (TypeScript 超集)
- UI 框架：ArkUI (声明式 UI)
- 数据存储：关系型数据库 (RDB) + 首选项 (Preferences)
- 网络：@ohos.net.http
- 开发工具：DevEco Studio 4.0+

## Goals / Non-Goals

**Goals:**
- 实现与 Web 版相同的核心业务功能
- 支持离线数据缓存
- 适配手机和平板两种设备形态
- 保持与现有后端 API 的兼容性
- 遵循 HarmonyOS 设计规范

**Non-Goals:**
- 不支持 HarmonyOS 分布式特性（跨设备流转）
- 不实现推送通知功能
- 不支持手表等穿戴设备
- 不开发桌面端应用

## Decisions

### 1. 采用分层架构

**选择:** UI 层 → Service 层 → Data 层 → API 层

**理由:**
- 与现有 Web 后端架构保持一致，便于理解
- 职责清晰，便于测试和维护
- 符合 HarmonyOS 推荐的最佳实践

**目录结构:**
```
dry_cleaning_system_HmOS/
├── entry/
│   └── src/
│       └── main/
│           ├── ets/
│           │   ├── entryability/  # 入口 Ability
│           │   ├── pages/         # 页面
│           │   ├── components/    # 组件
│           │   ├── services/      # 业务服务
│           │   ├── models/        # 数据模型
│           │   ├── api/           # API 接口
│           │   └── database/      # 数据库
│           └── resources/         # 资源文件
```

### 2. 使用 MVVM 模式

**选择:** Model-View-ViewModel 模式

**理由:**
- ArkUI 天然支持响应式数据绑定
- 便于状态管理和数据流控制
- 与现有 Web 前端的 MVC 模式有相似性

### 3. 数据同步策略

**选择:** 网络优先 + 本地缓存

**理由:**
- 保证数据实时性
- 支持离线浏览最近数据
- 网络恢复后自动同步

**实现:**
- 使用 RDB 存储客户、订单等核心数据
- 使用 Preferences 存储用户设置
- 每次操作先更新本地，再同步到服务器

### 4. API 兼容性

**选择:** 完全复用现有 RESTful API

**理由:**
- 无需修改后端代码
- 保持数据一致性
- 降低开发成本

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| HarmonyOS 生态不成熟 | 第三方库少，遇到问题难解决 | 尽量使用官方 SDK，减少第三方依赖 |
| 开发人员学习成本 | ArkTS 和 ArkUI 需要学习 | 提供详细文档和代码示例 |
| 网络请求失败 | 离线时无法获取最新数据 | 实现本地缓存和重试机制 |
| 设备适配问题 | 不同屏幕尺寸显示异常 | 使用响应式布局和栅格系统 |
| 数据同步冲突 | 多设备同时修改数据 | 以后端数据为准，提示用户刷新 |

## Migration Plan

1. **环境搭建** (1 周)
   - 安装 DevEco Studio
   - 配置 HarmonyOS SDK
   - 创建项目骨架

2. **基础架构** (1 周)
   - 实现网络请求封装
   - 实现数据库封装
   - 实现通用组件库

3. **功能开发** (4 周)
   - 第 1 周：客户管理模块
   - 第 2 周：订单管理模块
   - 第 3 周：会员充值模块
   - 第 4 周：统计报表模块

4. **测试优化** (1 周)
   - 功能测试
   - 性能优化
   - UI/UX 优化

5. **发布上线** (1 周)
   - 应用签名
   - 提交华为应用市场
   - 准备用户文档

**回滚策略:** 保留 Web 版本，HarmonyOS 应用作为可选方案

## Open Questions

- 是否需要支持生物识别登录？
- 是否需要实现图片压缩上传？
- 是否需要支持多语言？
- 数据存储是否需要加密？
