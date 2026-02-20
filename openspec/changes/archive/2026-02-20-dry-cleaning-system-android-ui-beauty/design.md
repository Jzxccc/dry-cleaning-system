## Context

当前 Android 客户端使用基础的 UI 设计，缺乏现代化的视觉风格和交互体验。项目基于 Android Jetpack 和 Material Components 构建，需要升级到 Material Design 3 设计规范以提升整体用户体验。

**背景:**
- 干洗店管理系统运行在平板设备上，使用触摸屏操作
- 现有 UI 功能正常但视觉效果较为简单
- 需要保持现有业务逻辑不变，仅优化 UI 呈现

**约束:**
- 保持现有功能完整性，不改变业务流程
- 兼容现有 Android 版本（API 21+）
- 不影响数据库结构和后端逻辑

## Goals / Non-Goals

**Goals:**
- 实现 Material Design 3 主题系统（颜色、字体、形状）
- 优化主要页面的视觉布局和组件样式
- 提升触摸交互的视觉反馈
- 保持代码可维护性和扩展性

**Non-Goals:**
- 不改变现有业务逻辑和功能
- 不重构底层架构或数据层
- 不添加新的功能模块
- 不涉及 iOS 或其他平台

## Decisions

### Decision 1: 使用 Material Design 3 (Material You)
**选择:** 采用 Material Components for Android 1.9+ 和 Material Design 3 规范

**理由:**
- Google 官方推荐的最新设计语言
- 与现有 Material Components 兼容，迁移成本低
- 提供动态颜色系统和自适应主题支持
- 丰富的组件库和完善的文档

**替代方案:**
- 自定义 UI 库：开发成本高，维护困难
- 第三方 UI 框架：增加依赖风险，学习成本

### Decision 2: 主题颜色方案
**选择:** 使用蓝色系作为主色调，搭配中性色

**理由:**
- 蓝色传达专业、清洁的品牌形象
- 符合干洗行业视觉习惯
- 提供良好的可访问性和对比度

**颜色定义:**
- Primary: #2196F3 (Material Blue 500)
- Primary Variant: #1976D2 (Material Blue 700)
- Secondary: #03DAC6 (Material Teal 200)
- Background: #FFFFFF / #121212 (深色模式)
- Surface: #FFFFFF / #1E1E1E (深色模式)

### Decision 3: 组件样式策略
**选择:** 使用 Material Theme Overlay 和自定义 Style

**理由:**
- 集中管理样式，便于维护
- 支持运行时主题切换
- 与 Android 原生组件无缝集成

**实现方式:**
- 在 `themes.xml` 中定义全局样式
- 使用 `MaterialButton`、`CardView` 等标准组件
- 通过 `styles.xml` 定制组件外观

### Decision 4: 布局优化
**选择:** 使用 ConstraintLayout 和 Material 组件

**理由:**
- 扁平化布局 hierarchy，提升性能
- 响应式设计，适配不同屏幕尺寸
- 支持动画和过渡效果

## Risks / Trade-offs

**[Risk] 颜色对比度不足** → 使用 Material Design 对比度检查工具确保可访问性

**[Risk] 旧设备兼容性问题** → 最低支持 API 21，使用 AndroidX 兼容库

**[Risk] 样式冲突** → 建立样式命名规范，避免与现有代码冲突

**[Trade-off] 增加 APK 体积** → Material Components 库增加约 500KB，可接受

**[Trade-off] 学习成本** → 团队需要熟悉 Material Design 3 规范，但有官方文档支持
