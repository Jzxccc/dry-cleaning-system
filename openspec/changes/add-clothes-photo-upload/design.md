## Context

当前系统在创建订单时只能添加衣物类型和价格，无法记录衣物的实际状态。当顾客取衣时声称衣物有破损，店家无法提供接收衣物时的状态证据，容易产生纠纷和赔偿损失。

**背景:**
- 现有订单流程：选择客户 → 添加衣物 → 选择支付方式 → 提交
- 衣物信息只包含类型和价格
- 无照片记录功能

**约束:**
- 照片仅用于大致参考，不需要高精度
- 存储在本地，节省空间
- 订单完成后自动删除

## Goals / Non-Goals

**Goals:**
- 在创建订单时支持为衣物拍照
- 照片以缩略图形式存储（小尺寸）
- 照片与订单关联
- 订单完成后自动清理照片
- 简化拍照流程，快速操作

**Non-Goals:**
- 不需要专业摄影质量
- 不需要照片编辑功能
- 不需要云端同步
- 不需要相册管理功能

## Decisions

### Decision 1: 拍照时机
**选择:** 在创建订单页面（NewOrderScreen），添加衣物列表下方添加拍照按钮

**理由:**
- 用户在添加衣物时自然想到拍照
- 流程连贯，不会遗漏
- 一次可以拍多件衣物的照片

**替代方案:**
- 在订单详情页拍照：需要额外页面，流程复杂
- 在客户选择后拍照：无法对应具体衣物

### Decision 2: 照片存储方式
**选择:** 使用 Android CameraX API 拍照，缩略图存储在应用私有目录

**技术栈:**
- CameraX: `androidx.camera:camera-*` (1.3.0+)
- 存储路径：`context.filesDir/photos/order_{orderId}/`
- 缩略图尺寸：200x200 像素
- 格式：JPEG，质量 80%

**理由:**
- CameraX 是 Jetpack 官方推荐方案，与 Compose 兼容性好
- 缩略图节省空间（约 10-50KB/张）
- 应用私有目录无需存储权限，卸载时自动清理

**依赖配置:**
```kotlin
implementation("androidx.camera:camera-core:1.3.0")
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")
```

### Decision 3: 照片与订单关联
**选择:** 在 Order 实体类添加 photoPath 字段（String 类型，存储目录路径）

**数据库变更:**
```kotlin
// Order.kt
@Entity(tableName = "orders")
data class Order(
    // ... 现有字段
    @ColumnInfo(name = "photo_path") val photoPath: String? = null
)
```

**Room Migration:**
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE orders ADD COLUMN photo_path TEXT")
    }
}
```

**理由:**
- 简单直接，一个订单一个照片目录
- 查询订单时可直接获取照片路径
- 删除订单时同步删除目录

### Decision 4: 照片清理策略
**选择:** 订单状态变为 FINISHED 时自动删除照片目录

**实现位置:** OrderViewModel.kt 的 updateOrderStatus 方法

**理由:**
- 订单完成意味着衣物已取走，纠纷风险消除
- 自动清理避免存储空间浪费
- 在 ViewModel 层处理，逻辑集中

### Decision 5: 权限处理
**选择:** 使用 Activity Result API 请求相机权限

**实现:**
```kotlin
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) openCamera() else showPermissionDeniedMessage()
}
```

**理由:**
- 现代 Android 权限处理最佳实践
- 与 Compose 兼容性好
- 代码简洁清晰

## Risks / Trade-offs

**[Risk] 用户拒绝相机权限** → 提示用户拍照的重要性，允许跳过

**[Risk] 照片占用存储空间** → 使用缩略图（最大 200x200），单张约 10-50KB

**[Risk] 删除订单时照片未清理** → 在 Repository 层统一处理删除逻辑

**[Risk] 旧版本数据库不兼容** → 使用 Room 的 migration 机制升级数据库

**[Trade-off] 缩略图清晰度较低** → 可接受，仅用于大致参考
