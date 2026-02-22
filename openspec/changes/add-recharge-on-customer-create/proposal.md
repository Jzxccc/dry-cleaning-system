## Why

当前系统创建客户和储值充值是两个独立的操作流程，用户需要先在客户管理页面创建客户，然后再跳转到充值页面进行充值。这种分离的操作流程效率较低，特别是在新客户办卡充值的场景下，需要多次跳转和重复搜索客户。此变更旨在优化用户体验，在创建客户的同时支持直接充值，减少操作步骤。

## What Changes

- **新增**：在 Android 端客户创建对话框中增加"创建并充值"选项
- **新增**：Android 端创建客户后自动跳转到充值页面的功能
- **新增**：后端 API 支持创建客户时可选的初始充值金额参数
- **新增**：后端 API 创建客户时同时创建充值记录（如果包含充值）
- **修改**：CustomerController 的 createCustomer 接口支持可选的 rechargeAmount 参数

## Capabilities

### New Capabilities

- `customer-create-with-recharge`: 创建客户时支持同时充值的端到端能力

### Modified Capabilities

- `customer-api`: 创建客户的 API 需要支持可选的充值金额参数

## Impact

- **后端 API**: `POST /api/customers` 接口需要支持可选的 `rechargeAmount` 参数
- **Android 应用**: 客户创建 UI 需要增加充值选项和跳转逻辑
- **数据库**: 充值记录表会新增记录（当创建客户时包含充值）
- **用户体验**: 减少新客户办卡充值的操作步骤，从 4 步减少到 2 步
