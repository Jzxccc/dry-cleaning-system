## Context

当前干洗店管理系统已有客户管理（Customer）和订单管理（Order）模块，支持基础的 CRUD 操作和精确查询。但在实际业务场景中，操作员经常需要根据不完整的客户信息（如只记得姓名的一部分、手机号后几位）或订单信息（如订单号记不全、衣物类型）进行搜索。

**现有接口:**
- 客户查询：`GET /api/customers/search/phone/{phone}`（精确匹配）
- 客户查询：`GET /api/customers/search/name/{name}`（精确匹配）
- 订单查询：`GET /api/orders/customer/{customerId}`（按 ID 精确匹配）

**技术栈:** Spring Boot + MyBatis-Plus + MySQL

## Goals / Non-Goals

**Goals:**
- 实现客户模糊搜索接口，支持姓名、手机号、备注字段的 LIKE 查询
- 实现订单模糊搜索接口，支持订单号、客户姓名、衣物类型的模糊查询
- 支持多条件组合查询（AND 逻辑）
- 保持与现有代码风格一致，使用 MyBatis-Plus 的 QueryWrapper
- 返回标准 RESTful 响应格式

**Non-Goals:**
- 不支持全文搜索引擎（如 Elasticsearch）
- 不支持拼音搜索或同义词匹配
- 不修改现有精确查询接口（保持向后兼容）
- 不实现复杂的排序和分页（后续迭代可添加）

## Decisions

### 1. 使用 MyBatis-Plus QueryWrapper 实现模糊查询

**选择:** 使用 `QueryWrapper` 的 `like()` 方法构建动态查询条件

**理由:**
- 项目已使用 MyBatis-Plus 作为 ORM 框架
- QueryWrapper 支持动态条件拼接，代码简洁
- 与现有 Service 层代码风格一致
- 无需额外依赖或 XML 配置

**替代方案:**
- 方案 A: 手写 XML Mapper - 过于繁琐，不符合项目现有模式
- 方案 B: 使用 JPA Specification - 需要引入新依赖，学习成本高

### 2. 接口设计采用查询参数而非路径参数

**选择:** `GET /api/customers/search/fuzzy?name=xxx&phone=xxx`

**理由:**
- 支持可选参数，用户可传入任意组合条件
- 符合 RESTful 查询语义
- 便于后续扩展更多过滤条件

**替代方案:**
- 方案 A: `POST /api/customers/search` + JSON body - 适合复杂查询，但当前场景过于重量级

### 3. 模糊匹配使用 `%keyword%` 模式

**选择:** 前后都加通配符，实现包含匹配

**理由:**
- 最灵活的匹配方式，用户体验最佳
- 符合"模糊搜索"的直观预期

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| LIKE 查询性能问题 | 大数据量时查询变慢 | 当前数据量小，后续可在姓名字段添加索引 |
| SQL 注入风险 | 安全隐患 | 使用 QueryWrapper 参数化查询，不拼接 SQL 字符串 |
| 特殊字符处理 | `%`、`_` 等字符可能匹配异常 | 后续可添加转义逻辑，当前版本暂不处理 |

## Migration Plan

1. 在 CustomerMapper 和 OrderMapper 中添加模糊查询方法
2. 在 CustomerService 和 OrderService 中实现业务逻辑
3. 在 CustomerController 和 OrderController 中添加新接口
4. 使用 Postman 或前端进行接口测试
5. 验证现有功能不受影响

**回滚策略:** 删除新增的接口和 Mapper 方法，不影响现有代码

## Open Questions

- 是否需要添加分页参数？（当前返回全部匹配结果）
- 是否需要限制最大返回数量？
- 是否需要支持按匹配度排序？
