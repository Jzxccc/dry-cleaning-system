## Why

当前订单创建页面和订单详情页面已支持上传照片并显示缩略图，但用户无法点击查看完整尺寸的大图。这导致用户无法清晰查看衣物细节，特别是在检查衣物状况或损坏时，需要更清晰的视图。此变更旨在添加点击图片放大查看的功能，提升用户体验。

## What Changes

- **新增**：在 NewOrderScreen 点击缩略图可打开全屏放大对话框
- **新增**：在 OrderDetailScreen 点击缩略图可打开全屏放大对话框
- **新增**：放大对话框支持双指缩放和滑动浏览
- **新增**：放大对话框显示图片导航（多张图片时可左右滑动切换）

## Capabilities

### New Capabilities

- `photo-zoom-viewer`: 支持点击缩略图放大查看的端到端能力

### Modified Capabilities

- `order-ui`: 订单创建和详情 UI 需要支持点击缩略图放大查看

## Impact

- **Android 应用**: NewOrderScreen 和 OrderDetailScreen 需要添加全屏查看照片对话框
- **用户体验**: 用户可以清晰查看衣物照片细节，支持缩放和导航
- **依赖**: 使用现有的 Coil 图片加载库，无需添加新依赖
