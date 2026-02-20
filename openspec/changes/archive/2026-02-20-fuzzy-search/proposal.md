## Why

当前系统的客户和订单查询接口仅支持精确匹配（如通过 ID、手机号、姓名精确查询），无法满足实际业务中用户输入不完整或模糊信息的搜索场景。添加模糊查询能力可以提升用户查找客户和订单的效率。

## What Changes

- 新增客户模糊查询接口，支持按姓名、手机号、备注等字段的模糊匹配
- 新增订单模糊查询接口，支持按订单号、客户姓名、衣物类型等字段的模糊匹配
- 支持多条件组合模糊搜索
- 返回分页结果集

## Capabilities

### New Capabilities
- `fuzzy-customer-search`: 客户模糊搜索能力，支持按姓名、手机号、备注等字段进行 LIKE 查询，返回匹配的客户列表
- `fuzzy-order-search`: 订单模糊搜索能力，支持按订单号、客户姓名、衣物类型等字段进行模糊查询，返回匹配的订单列表

### Modified Capabilities
<!-- 无现有能力需要修改 -->

## Impact

- **新增接口**:
  - `GET /api/customers/search/fuzzy` - 客户模糊搜索
  - `GET /api/orders/search/fuzzy` - 订单模糊搜索
- **新增 Mapper 方法**: CustomerMapper 和 OrderMapper 需要添加模糊查询方法
- **新增 Service 方法**: CustomerService 和 OrderService 需要添加模糊搜索业务逻辑
- **数据库**: 无 schema 变更，仅使用现有表的 LIKE 查询
