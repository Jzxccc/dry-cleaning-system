## 1. Mapper 层实现

- [x] 1.1 在 CustomerMapper 中添加 fuzzySearch 方法，使用 QueryWrapper 支持 name/phone/note 模糊查询
- [x] 1.2 在 OrderMapper 中添加 fuzzySearch 方法，使用 QueryWrapper 支持 orderNo/customerName/clothesType 模糊查询

## 2. Service 层实现

- [x] 2.1 在 CustomerService 接口中添加 fuzzySearch 方法定义
- [x] 2.2 在 CustomerServiceImpl 中实现 fuzzySearch 方法，处理多条件组合查询逻辑
- [x] 2.3 在 OrderService 接口中添加 fuzzySearch 方法定义
- [x] 2.4 在 OrderServiceImpl 中实现 fuzzySearch 方法，处理多条件组合查询逻辑

## 3. Controller 层实现

- [x] 3.1 在 CustomerController 中添加 GET /api/customers/search/fuzzy 接口
- [x] 3.2 在 OrderController 中添加 GET /api/orders/search/fuzzy 接口
- [x] 3.3 为两个接口添加请求参数校验和异常处理
- [x] 3.4 在 CustomerController 中添加 GET /api/customers/search/name-or-pinyin 接口（拼音搜索）
- [x] 3.5 修改 fuzzySearch 接口支持拼音匹配（fuzzySearchWithPinyin）

## 4. 前端集成

- [x] 4.1 在 customers.html 添加模糊搜索 UI（支持拼音）
- [x] 4.2 在 search-order.html 添加模糊搜索 UI
- [x] 4.3 在 new-order.html 添加手机号和姓名/拼音搜索功能
- [x] 4.4 在 new-order.html 实现多客户选择确认功能

## 5. 测试验证

- [x] 5.1 使用 Postman 测试客户模糊搜索接口（单条件、多组合）
- [x] 5.2 使用 Postman 测试订单模糊搜索接口（单条件、多组合）
- [x] 5.3 验证现有接口功能不受影响
- [x] 5.4 验证边界情况：空参数、特殊字符、无匹配结果
- [x] 5.5 测试拼音搜索功能（如：zs 搜索张三）
- [x] 5.6 测试多客户选择确认功能
