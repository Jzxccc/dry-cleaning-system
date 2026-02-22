## 1. 后端 API 修改

- [x] 1.1 修改 `CustomerController.createCustomer` 方法，添加可选的 `rechargeAmount` 参数
- [x] 1.2 在 `CustomerService` 中实现创建客户时充值的业务逻辑（含 20% 赠送计算）
- [x] 1.3 确保创建客户和充值记录在同一 `@Transactional` 事务中
- [x] 1.4 添加充值金额验证（必须是 100 的整数倍）
- [x] 1.5 更新 `CustomerMapper` 和 `RechargeRecordMapper` 如有需要

## 2. Android UI 修改

- [x] 2.1 修改 `CustomerEditDialog` 组件，增加"充值金额"输入框
- [x] 2.2 在 `CustomerEditDialog` 中增加"创建并充值"按钮
- [x] 2.3 实现充值金额输入验证（100 的整数倍）
- [x] 2.4 显示充值计算信息（充值金额、赠送金额、实际到账金额）
- [x] 2.5 更新 `CustomerViewModel` 支持带充值的客户创建

## 3. 测试验证

- [x] 3.1 测试后端 API：创建客户不带充值（向后兼容）
- [x] 3.2 测试后端 API：创建客户带充值（验证余额计算和记录创建）
- [x] 3.3 测试后端 API：创建客户带无效充值金额（验证错误处理）
- [x] 3.4 测试 Android 端：创建客户不带充值
- [x] 3.5 测试 Android 端：创建客户带充值（验证 UI 跳转和余额更新）
- [x] 3.6 验证充值记录正确保存到数据库

## 4. 修改充值赠送比例

- [x] 4.1 修改充值赠送比例为阶梯式：100 送 10%，200 送 20%
- [x] 4.2 增加快捷充值按钮（100、200、500、1000）
