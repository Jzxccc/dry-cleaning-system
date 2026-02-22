## Context

现有订单创建页面（NewOrderScreen）和订单详情页面（OrderDetailScreen）已支持：
- 拍照上传衣物照片
- 生成 200x200 缩略图
- 在 UI 中横向滚动显示缩略图列表

技术栈：
- **Jetpack Compose**: UI 框架
- **Coil**: 图片加载库（已集成）
- **现有缩略图**: 存储在 `/photos/order_{orderId}/` 目录，命名格式 `{timestamp}_thumb.jpg`
- **原图**: 同目录下 `{timestamp}.jpg`

## Goals / Non-Goals

**Goals:**
- 点击缩略图可打开全屏放大对话框
- 支持双指缩放查看图片细节
- 支持滑动切换多张图片
- 在 NewOrderScreen 和 OrderDetailScreen 同时支持
- 保持与现有代码风格一致

**Non-Goals:**
- 不修改图片存储逻辑
- 不修改缩略图生成逻辑
- 不添加图片编辑功能
- 不涉及后端 API 变更

## Decisions

### 1. 图片加载策略

**决策**：放大时加载原图（非缩略图）

**理由**：
- 原图已存在于 `/photos/order_{orderId}/` 目录
- 原图路径只需将 `_thumb.jpg` 替换为 `.jpg`
- 无需额外生成大图，节省存储空间

**备选方案**：
- 重新生成中等尺寸图片 → 增加复杂度，不采用

### 2. 图片放大组件选择

**决策**：使用 Coil + Compose 原生手势实现缩放功能

**理由**：
- Coil 已集成在项目中使用，无需添加新依赖
- Compose 的 `pointerInput` 提供手势检测能力
- 保持技术栈统一，减少维护成本

**备选方案**：
- 使用第三方库如 PhotoView → 增加依赖，不采用
- 使用 Android WebView 显示图片 → 过度复杂，不采用

### 3. UI 交互设计

**决策**：使用 AlertDialog 显示放大图片

**理由**：
- AlertDialog 是 Compose 原生组件
- 自动处理对话框生命周期
- 支持背景遮罩和返回导航

**备选方案**：
- 使用独立 Activity → 过度重量级，不采用
- 使用 Navigation 到新页面 → 增加导航复杂度，不采用

### 4. 多张图片导航

**决策**：使用 LazyRow 横向滚动切换图片

**理由**：
- 与现有缩略图列表风格一致
- Compose LazyRow 性能优秀
- 实现简单，易于维护

## Risks / Trade-offs

**[风险]** 大图片加载可能导致内存溢出 → **缓解**：Coil 自动处理图片缩放和内存管理

**[风险]** 手势冲突（缩放 vs 滑动）→ **缓解**：使用 Compose 手势优先级处理

**[风险]** 对话框中图片加载延迟 → **缓解**：显示加载进度指示器

**[权衡]** 使用 AlertDialog 可能限制自定义动画 → 接受，优先保证功能稳定性
