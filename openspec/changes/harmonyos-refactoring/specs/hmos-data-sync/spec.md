## ADDED Requirements

### Requirement: 系统可以访问后端 API
系统应当支持与现有 Spring Boot 后端的 RESTful API 通信。

#### Scenario: 成功发起 HTTP 请求
- **WHEN** 应用需要获取或提交数据
- **THEN** 系统使用@ohos.net.http 发起 HTTP 请求

#### Scenario: 处理 API 响应
- **WHEN** 收到 API 响应
- **THEN** 系统解析 JSON 数据并更新 UI

#### Scenario: 处理网络错误
- **WHEN** 网络请求失败
- **THEN** 系统显示错误提示并支持重试

### Requirement: 系统可以缓存本地数据
系统应当支持使用关系型数据库缓存核心业务数据。

#### Scenario: 初始化数据库
- **WHEN** 应用首次启动
- **THEN** 系统创建 customer、orders、clothes、recharge_record 等表

#### Scenario: 缓存客户数据
- **WHEN** 获取客户列表
- **THEN** 系统将数据存储到 RDB 以便离线访问

#### Scenario: 缓存订单数据
- **WHEN** 获取订单列表
- **THEN** 系统将数据存储到 RDB 以便离线访问

### Requirement: 系统可以同步数据
系统应当支持本地数据与服务器数据的同步。

#### Scenario: 网络优先策略
- **WHEN** 网络可用时
- **THEN** 系统从服务器获取最新数据并更新本地缓存

#### Scenario: 离线模式
- **WHEN** 网络不可用时
- **THEN** 系统显示本地缓存的数据

#### Scenario: 数据更新同步
- **WHEN** 用户在本地添加或修改数据
- **THEN** 系统先更新本地缓存，再同步到服务器

### Requirement: 系统可以存储用户设置
系统应当支持使用 Preferences 存储用户偏好设置。

#### Scenario: 存储用户设置
- **WHEN** 用户修改设置
- **THEN** 系统使用 Preferences 保存设置

#### Scenario: 读取用户设置
- **WHEN** 应用启动时
- **THEN** 系统从 Preferences 读取用户设置
