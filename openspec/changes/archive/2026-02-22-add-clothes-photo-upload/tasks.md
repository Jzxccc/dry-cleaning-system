## 1. 数据库变更

- [x] 1.1 在 Order.kt 实体类添加 photoPath 字段（String? 类型，@ColumnInfo(name = "photo_path")）
- [x] 1.2 在 OrderDao.kt 添加更新 photoPath 的 @Query 方法
- [x] 1.3 创建 Room Migration 添加 photo_path 列（版本升级）

## 2. CameraX 配置

- [x] 2.1 在 app/build.gradle.kts 添加 CameraX 依赖
- [x] 2.2 在 AndroidManifest.xml 添加相机权限声明
- [ ] 2.3 在 MainActivity 请求相机权限

## 3. 相机功能实现

- [x] 3.1 创建 CameraPreview Composable（相机预览界面）
- [x] 3.2 创建 CameraHelper 工具类（拍照、缩略图生成）
- [x] 3.3 实现照片存储逻辑（context.filesDir/photos/order_{orderId}/）
- [x] 3.4 实现缩略图生成（200x200，JPEG 80% 质量）

## 4. UI 实现

- [x] 4.1 在 NewOrderScreen 添加拍照按钮（衣物列表下方）
- [x] 4.2 实现拍照对话框（全屏相机预览）
- [x] 4.3 添加照片缩略图显示区域（横向滚动列表）
- [ ] 4.4 实现全屏查看照片对话框

## 5. 业务逻辑

- [x] 5.1 在 OrderViewModel 添加拍照处理方法
- [x] 5.2 在 OrderRepository 添加照片路径保存方法
- [x] 5.3 在 OrderViewModel 实现订单完成时删除照片逻辑
- [x] 5.4 在 OrderRepository 实现订单删除时同步删除照片逻辑

## 6. 权限处理

- [x] 6.1 使用 ActivityResultContracts.RequestPermission 请求相机权限
- [x] 6.2 处理权限被拒绝的情况（显示提示，允许跳过）

## 7. 测试和验证

- [ ] 7.1 测试拍照功能（各种光线条件）
- [ ] 7.2 测试缩略图存储和加载
- [ ] 7.3 测试订单完成时照片自动删除
- [ ] 7.4 测试权限请求流程
