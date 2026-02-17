# 干洗店管理系统（Level 1）需求文档

---

# 1. Why（为什么要做）

当前干洗店使用手写本记录客户和衣物信息，存在以下问题：

1. 查找订单困难（按姓名翻本子）
2. 难以标记衣物是否已取
3. 洗好的衣物曾出现找不到对应订单的情况
4. 储值余额计算麻烦
5. 难以统计每日收入

为提升效率、减少人工错误、简化管理流程，需要开发一个轻量级干洗店管理系统（网页 + 平板使用）。

---

# 2. What Changes（做什么）

本项目将实现一个 Level 1 干洗店管理系统，包含：

- 客户管理
- 订单管理
- 衣物管理
- 衣物状态流转
- 储值充值与支付
- 收入统计
- 未取订单管理

数据库使用 SQLite。

---

# 3. Scope（范围）

## 本阶段包含：

- 新建订单
- 搜索订单（按姓名 / 手机号）
- 衣物状态管理（未洗 / 已洗 / 已取）
- 储值功能（充值 100 赠送 20%）
- 收入统计
- 未取订单列表

## 不包含：

- 打印小票
- 微信通知
- 多门店
- 权限系统
- 云端备份
- 小程序

---

# 4. 核心业务流程

## 4.1 收衣流程

1. 输入姓名或手机号
2. 若客户不存在 → 创建客户
3. 添加衣物：
   - 类型（下拉选择）
   - 默认价格自动填充（可修改）
   - 破损备注
   - 破损图片（可选）
4. 自动计算总价
5. 选择支付方式：
   - 现金
   - 储值
   - 未支付
6. 生成订单
7. 默认状态为：UNWASHED

---

## 4.2 状态流转

订单状态：

- UNWASHED（未洗）
- WASHED（已洗）
- FINISHED（已取）

状态流转：

UNWASHED → WASHED → FINISHED

衣物状态与订单保持一致。

---

## 4.3 储值规则

充值规则：

- 充值金额 = X
- 赠送金额 = X * 0.2
- 实际到账 = X + 赠送金额

示例：

充值 100 → 余额增加 120

支付规则：

- 若余额 ≥ 订单金额 → 自动扣除
- 若余额不足 → 提示

---

# 5. 数据模型设计（SQLite）

SQLite 说明：

- 使用 INTEGER PRIMARY KEY AUTOINCREMENT
- 金额统一使用 REAL 类型
- 时间使用 TEXT（ISO 8601 格式）

---

## 5.1 客户表

    CREATE TABLE customer (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        phone TEXT,
        wechat TEXT,
        balance REAL DEFAULT 0,
        create_time TEXT NOT NULL
    );

---

## 5.2 订单表

    CREATE TABLE orders (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        order_no TEXT NOT NULL UNIQUE,
        customer_id INTEGER NOT NULL,
        total_price REAL NOT NULL,
        prepaid REAL DEFAULT 0,
        pay_type TEXT,
        urgent INTEGER DEFAULT 0,
        status TEXT NOT NULL,
        expected_time TEXT,
        create_time TEXT NOT NULL,
        FOREIGN KEY (customer_id) REFERENCES customer(id)
    );

---

## 5.3 衣物表

    CREATE TABLE clothes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        order_id INTEGER NOT NULL,
        type TEXT NOT NULL,
        price REAL NOT NULL,
        damage_remark TEXT,
        damage_image TEXT,
        status TEXT NOT NULL,
        FOREIGN KEY (order_id) REFERENCES orders(id)
    );

---

## 5.4 储值记录表

    CREATE TABLE recharge_record (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        customer_id INTEGER NOT NULL,
        recharge_amount REAL NOT NULL,
        gift_amount REAL NOT NULL,
        create_time TEXT NOT NULL,
        FOREIGN KEY (customer_id) REFERENCES customer(id)
    );

---

# 6. 默认价格规则

| 类型 | 默认价格 |
|------|----------|
| 毛衫 | 20 |
| 羊绒大衣(小) | 20 |
| 羊绒大衣(中) | 25 |
| 羊绒大衣(大) | 30 |
| 皮毛一体 | 50 起 |
| 貂 | 300 起 |
| 鞋 | 15 起 |
| 裤子 | 20 |

价格默认自动填充，但允许人工修改。

---

# 7. 统计功能

系统需支持：

- 今日收入
- 本月收入
- 现金收入
- 储值收入
- 未取订单数量

统计按订单创建时间计算。

---

# 8. 页面结构

系统包含以下页面：

1. 新建订单
2. 未取订单列表
3. 搜索订单
4. 会员充值
5. 统计页面

UI 要求：

- 大按钮
- 大字体
- 简洁操作
- 适合平板触控

---

# 9. 非功能需求

- 响应时间 < 1 秒
- 单门店使用
- 不要求高并发
- SQLite 本地数据库
- 支持平板浏览器

---

# 10. 未来扩展（非本阶段）

- 小票打印
- 微信通知
- 会员等级
- 多门店
- 数据导出
