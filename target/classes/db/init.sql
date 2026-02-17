-- 干洗店管理系统数据库初始化脚本

-- 客户表
CREATE TABLE IF NOT EXISTS customer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT,
    wechat TEXT,
    balance REAL DEFAULT 0,
    create_time TEXT NOT NULL
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
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

-- 衣物表
CREATE TABLE IF NOT EXISTS clothes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id TEXT NOT NULL,
    type TEXT NOT NULL,
    price REAL NOT NULL,
    damage_remark TEXT,
    damage_image TEXT,
    status TEXT NOT NULL,
    create_time TEXT NOT NULL
);

-- 充值记录表
CREATE TABLE IF NOT EXISTS recharge_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    recharge_amount REAL NOT NULL,
    gift_amount REAL NOT NULL,
    create_time TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customer(phone);
CREATE INDEX IF NOT EXISTS idx_customer_name ON customer(name);
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_create_time ON orders(create_time);
CREATE INDEX IF NOT EXISTS idx_clothes_order_id ON clothes(order_id);
CREATE INDEX IF NOT EXISTS idx_clothes_status ON clothes(status);
CREATE INDEX IF NOT EXISTS idx_recharge_record_customer_id ON recharge_record(customer_id);
