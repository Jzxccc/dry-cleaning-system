## Context

现有干洗店管理系统采用 Spring Boot + Thymeleaf 架构，运行在浏览器中。为了在 Android 平板上离线使用，需要改造成 Android 原生应用。

**现有技术栈:**
- 后端：Spring Boot 2.7.0 + MyBatis-Plus + SQLite
- 前端：Thymeleaf + Bootstrap + JavaScript
- 数据库：SQLite 文件数据库

**目标技术栈:**
- Android SDK: API 21+ (Android 5.0+)
- 语言：Java
- UI 框架：Android View 系统
- 数据库：SQLite + Room Persistence Library

## Goals / Non-Goals

**Goals:**
- 实现与 Web 版相同的核心业务功能
- 支持离线数据存储和管理
- 保持与现有数据库结构兼容
- 适配平板屏幕（10 英寸左右）

**Non-Goals:**
- 不支持手机竖屏模式（仅平板横屏）
- 不支持云端同步（纯本地应用）
- 不支持多用户登录（单机使用）

## Decisions

### 1. 使用 Room 数据库框架

**选择:** 使用 Room 而非原生 SQLite API

**理由:**
- 编译时 SQL 检查，减少运行时错误
- 支持 LiveData，自动更新 UI
- 与现有 SQLite 数据库兼容
- 代码更简洁，易于维护

**替代方案:**
- 方案 A: 原生 SQLite API - 代码繁琐，容易出错
- 方案 B: GreenDAO - 已停止维护

### 2. 采用 MVVM 架构

**选择:** Model-View-ViewModel 模式

**理由:**
- 业务逻辑与 UI 分离
- 支持 LiveData 数据绑定
- 便于单元测试
- 符合 Android 开发最佳实践

### 3. 数据库迁移策略

**选择:** 复用现有数据库结构

**理由:**
- 保持数据格式一致
- 便于未来数据导入导出
- 无需修改现有业务逻辑

**迁移步骤:**
1. 从 Web 版导出 SQLite 数据库文件
2. 复制到 Android 设备
3. Room 自动适配现有表结构

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 平板性能限制 | 大数据量时可能卡顿 | 使用分页加载，限制单次查询数量 |
| 数据丢失风险 | 设备损坏导致数据丢失 | 定期导出备份到 SD 卡 |
| 屏幕适配问题 | 不同平板分辨率不同 | 使用 ConstraintLayout 自适应布局 |
| Android 版本兼容性 | 老版本 Android 不支持新特性 | 最低支持 API 21，使用兼容库 |

## Migration Plan

1. **项目搭建** (1 周)
   - 创建 Android 项目
   - 配置 Room 依赖
   - 创建数据库实体类

2. **核心功能开发** (3 周)
   - 第 1 周：客户管理模块
   - 第 2 周：订单管理模块
   - 第 3 周：会员充值和统计报表

3. **UI 适配** (1 周)
   - 平板布局优化
   - 横屏模式适配
   - 触摸交互优化

4. **测试和优化** (1 周)
   - 功能测试
   - 性能优化
   - Bug 修复

**回滚策略:** 保留 Web 版本，Android 版作为补充

## Open Questions

- 是否需要支持数据导出功能？
- 是否需要支持打印小票？
- 是否需要支持扫码枪输入？
