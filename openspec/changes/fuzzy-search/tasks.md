## 1. Mapper 层实现

- [ ] 1.1 在 CustomerMapper 中添加 fuzzySearch 方法，使用 QueryWrapper 支持 name/phone/note 模糊查询
- [ ] 1.2 在 OrderMapper 中添加 fuzzySearch 方法，使用 QueryWrapper 支持 orderNo/customerName/clothesType 模糊查询

## 2. Service 层实现

- [ ] 2.1 在 CustomerService 接口中添加 fuzzySearch 方法定义
- [ ] 2.2 在 CustomerServiceImpl 中实现 fuzzySearch 方法，处理多条件组合查询逻辑
- [ ] 2.3 在 OrderService 接口中添加 fuzzySearch 方法定义
- [ ] 2.4 在 OrderServiceImpl 中实现 fuzzySearch 方法，处理多条件组合查询逻辑

## 3. Controller 层实现

- [ ] 3.1 在 CustomerController 中添加 GET /api/customers/search/fuzzy 接口
- [ ] 3.2 在 OrderController 中添加 GET /api/orders/search/fuzzy 接口
- [ ] 3.3 为两个接口添加请求参数校验和异常处理

## 4. 测试验证

- [ ] 4.1 使用 Postman 测试客户模糊搜索接口（单条件、多组合）
- [ ] 4.2 使用 Postman 测试订单模糊搜索接口（单条件、多组合）
- [ ] 4.3 验证现有接口功能不受影响
- [ ] 4.4 验证边界情况：空参数、特殊字符、无匹配结果
