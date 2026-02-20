## ADDED Requirements

### Requirement: 系统使用 SQLite 本地数据库
系统应当使用 SQLite 数据库存储所有业务数据。

#### Scenario: 数据库初始化
- **WHEN** 应用首次启动
- **THEN** 系统创建 customer、orders、clothes、recharge_record 表

#### Scenario: 数据库版本管理
- **WHEN** 数据库版本升级
- **THEN** 系统自动执行迁移脚本

### Requirement: 客户数据本地存储
系统应当支持在本地数据库中存储和查询客户信息。

#### Scenario: 保存客户
- **WHEN** 用户添加或编辑客户
- **THEN** 系统将客户数据保存到 customer 表

#### Scenario: 查询客户
- **WHEN** 用户查看客户列表或搜索客户
- **THEN** 系统从 customer 表查询数据

### Requirement: 订单数据本地存储
系统应当支持在本地数据库中存储和查询订单信息。

#### Scenario: 保存订单
- **WHEN** 用户创建或编辑订单
- **THEN** 系统将订单数据保存到 orders 表

#### Scenario: 查询订单
- **WHEN** 用户查看订单列表或搜索订单
- **THEN** 系统从 orders 表查询数据

### Requirement: 衣物数据本地存储
系统应当支持在本地数据库中存储和查询衣物信息。

#### Scenario: 保存衣物
- **WHEN** 用户添加衣物到订单
- **THEN** 系统将衣物数据保存到 clothes 表

#### Scenario: 查询衣物
- **WHEN** 用户查看订单详情
- **THEN** 系统从 clothes 表查询该订单的衣物列表

### Requirement: 充值记录本地存储
系统应当支持在本地数据库中存储和查询充值记录。

#### Scenario: 保存充值记录
- **WHEN** 用户进行充值操作
- **THEN** 系统将充值记录保存到 recharge_record 表

#### Scenario: 查询充值记录
- **WHEN** 用户查看充值历史
- **THEN** 系统从 recharge_record 表查询数据

### Requirement: 使用 Room 持久化库
系统应当使用 Room 库进行数据库操作。

#### Scenario: 定义实体类
- **WHEN** 创建数据库表
- **THEN** 使用@Entity 注解定义实体类

#### Scenario: 定义 DAO
- **WHEN** 定义数据访问接口
- **THEN** 使用@Dao 注解定义 CRUD 方法

#### Scenario: 定义数据库
- **WHEN** 创建数据库实例
- **THEN** 使用@Database 注解定义 Room 数据库
