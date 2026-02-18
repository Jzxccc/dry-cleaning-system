import relationalStore from '@ohos.data.relationalStore';
import { Customer } from '../models/Customer';
import { Order } from '../models/Order';
import { Context } from '@ohos.abilityAccessCtrl';

/**
 * 数据库配置
 */
const DB_CONFIG = {
  name: 'dry_cleaning.db',
  securityLevel: relationalStore.SecurityLevel.S1
};

/**
 * 表名和列名常量
 */
export const Tables = {
  CUSTOMER: 'customer',
  ORDERS: 'orders',
  CLOTHES: 'clothes',
  RECHARGE_RECORD: 'recharge_record'
};

export const CustomerColumns = {
  ID: 'id',
  NAME: 'name',
  PHONE: 'phone',
  WECHAT: 'wechat',
  BALANCE: 'balance',
  CREATE_TIME: 'create_time'
};

export const OrderColumns = {
  ID: 'id',
  ORDER_NO: 'order_no',
  CUSTOMER_ID: 'customer_id',
  TOTAL_PRICE: 'total_price',
  PREPAID: 'prepaid',
  PAY_TYPE: 'pay_type',
  URGENT: 'urgent',
  STATUS: 'status',
  EXPECTED_TIME: 'expected_time',
  CREATE_TIME: 'create_time'
};

/**
 * 数据库帮助类
 */
export class DatabaseHelper {
  private static instance: DatabaseHelper;
  private rdbStore: relationalStore.RdbStore | null = null;
  private isOnline: boolean = true;

  private constructor() {}

  static getInstance(): DatabaseHelper {
    if (!DatabaseHelper.instance) {
      DatabaseHelper.instance = new DatabaseHelper();
    }
    return DatabaseHelper.instance;
  }

  /**
   * 初始化数据库
   */
  async init(context: Context): Promise<void> {
    try {
      this.rdbStore = await relationalStore.getRdbStore(context, DB_CONFIG);
      await this.createTables();
      console.info('Database initialized successfully');
    } catch (error) {
      console.error('Failed to initialize database:', error);
      throw error;
    }
  }

  /**
   * 创建表
   */
  private async createTables(): Promise<void> {
    if (!this.rdbStore) return;

    // 创建客户表
    await this.rdbStore.executeSql(`
      CREATE TABLE IF NOT EXISTS ${Tables.CUSTOMER} (
        ${CustomerColumns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${CustomerColumns.NAME} TEXT NOT NULL,
        ${CustomerColumns.PHONE} TEXT,
        ${CustomerColumns.WECHAT} TEXT,
        ${CustomerColumns.BALANCE} REAL DEFAULT 0,
        ${CustomerColumns.CREATE_TIME} TEXT NOT NULL
      )
    `);

    // 创建订单表
    await this.rdbStore.executeSql(`
      CREATE TABLE IF NOT EXISTS ${Tables.ORDERS} (
        ${OrderColumns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${OrderColumns.ORDER_NO} TEXT NOT NULL UNIQUE,
        ${OrderColumns.CUSTOMER_ID} INTEGER NOT NULL,
        ${OrderColumns.TOTAL_PRICE} REAL NOT NULL,
        ${OrderColumns.PREPAID} REAL DEFAULT 0,
        ${OrderColumns.PAY_TYPE} TEXT,
        ${OrderColumns.URGENT} INTEGER DEFAULT 0,
        ${OrderColumns.STATUS} TEXT NOT NULL,
        ${OrderColumns.EXPECTED_TIME} TEXT,
        ${OrderColumns.CREATE_TIME} TEXT NOT NULL
      )
    `);

    // 创建衣物表
    await this.rdbStore.executeSql(`
      CREATE TABLE IF NOT EXISTS ${Tables.CLOTHES} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        order_id TEXT NOT NULL,
        type TEXT NOT NULL,
        price REAL NOT NULL,
        damage_remark TEXT,
        damage_image TEXT,
        status TEXT NOT NULL,
        create_time TEXT NOT NULL
      )
    `);

    // 创建充值记录表
    await this.rdbStore.executeSql(`
      CREATE TABLE IF NOT EXISTS ${Tables.RECHARGE_RECORD} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        customer_id INTEGER NOT NULL,
        recharge_amount REAL NOT NULL,
        gift_amount REAL NOT NULL,
        create_time TEXT NOT NULL
      )
    `);

    // 创建索引
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_customer_phone ON ${Tables.CUSTOMER}(phone)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_customer_name ON ${Tables.CUSTOMER}(name)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON ${Tables.ORDERS}(customer_id)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_orders_status ON ${Tables.ORDERS}(status)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_orders_create_time ON ${Tables.ORDERS}(create_time)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_clothes_order_id ON ${Tables.CLOTHES}(order_id)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_clothes_status ON ${Tables.CLOTHES}(status)
    `);
    await this.rdbStore.executeSql(`
      CREATE INDEX IF NOT EXISTS idx_recharge_record_customer_id ON ${Tables.RECHARGE_RECORD}(customer_id)
    `);

    console.info('Tables and indexes created successfully');
  }

  // ==================== 客户数据操作 ====================

  /**
   * 插入或更新客户
   */
  async saveCustomer(customer: Customer): Promise<number> {
    if (!this.rdbStore) return -1;

    try {
      // 检查客户是否已存在
      const existing = await this.getCustomerById(customer.id);
      
      if (existing) {
        // 更新
        const valueBucket = {
          [CustomerColumns.NAME]: customer.name,
          [CustomerColumns.PHONE]: customer.phone,
          [CustomerColumns.WECHAT]: customer.wechat,
          [CustomerColumns.BALANCE]: customer.balance,
          [CustomerColumns.CREATE_TIME]: customer.createTime
        };
        
        const predicates = new relationalStore.RdbPredicates(Tables.CUSTOMER);
        predicates.equalTo(CustomerColumns.ID, customer.id.toString());
        
        await this.rdbStore.update(valueBucket, predicates);
        console.info(`Customer updated: ${customer.id}`);
        return customer.id;
      } else {
        // 插入
        const valueBucket = {
          [CustomerColumns.NAME]: customer.name,
          [CustomerColumns.PHONE]: customer.phone,
          [CustomerColumns.WECHAT]: customer.wechat,
          [CustomerColumns.BALANCE]: customer.balance,
          [CustomerColumns.CREATE_TIME]: customer.createTime
        };
        
        const id = await this.rdbStore.insert(Tables.CUSTOMER, valueBucket);
        console.info(`Customer inserted: ${id}`);
        return id;
      }
    } catch (error) {
      console.error('Failed to save customer:', error);
      return -1;
    }
  }

  /**
   * 根据 ID 获取客户
   */
  async getCustomerById(id: number): Promise<Customer | null> {
    if (!this.rdbStore) return null;

    try {
      const predicates = new relationalStore.RdbPredicates(Tables.CUSTOMER);
      predicates.equalTo(CustomerColumns.ID, id.toString());
      
      const resultSet = await this.rdbStore.query(predicates);
      
      if (resultSet.goToFirstRow()) {
        const customer: Customer = {
          id: resultSet.getLong(resultSet.getColumnIndex(CustomerColumns.ID)),
          name: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.NAME)),
          phone: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.PHONE)),
          wechat: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.WECHAT)),
          balance: resultSet.getDouble(resultSet.getColumnIndex(CustomerColumns.BALANCE)),
          createTime: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.CREATE_TIME))
        };
        resultSet.close();
        return customer;
      }
      
      resultSet.close();
      return null;
    } catch (error) {
      console.error('Failed to get customer:', error);
      return null;
    }
  }

  /**
   * 获取所有客户
   */
  async getAllCustomers(): Promise<Customer[]> {
    if (!this.rdbStore) return [];

    try {
      const predicates = new relationalStore.RdbPredicates(Tables.CUSTOMER);
      const resultSet = await this.rdbStore.query(predicates);
      
      const customers: Customer[] = [];
      while (resultSet.goToNextRow()) {
        const customer: Customer = {
          id: resultSet.getLong(resultSet.getColumnIndex(CustomerColumns.ID)),
          name: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.NAME)),
          phone: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.PHONE)),
          wechat: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.WECHAT)),
          balance: resultSet.getDouble(resultSet.getColumnIndex(CustomerColumns.BALANCE)),
          createTime: resultSet.getString(resultSet.getColumnIndex(CustomerColumns.CREATE_TIME))
        };
        customers.push(customer);
      }
      
      resultSet.close();
      return customers;
    } catch (error) {
      console.error('Failed to get all customers:', error);
      return [];
    }
  }

  /**
   * 批量保存客户
   */
  async saveCustomers(customers: Customer[]): Promise<void> {
    if (!this.rdbStore) return;

    try {
      for (const customer of customers) {
        await this.saveCustomer(customer);
      }
      console.info(`Batch saved ${customers.length} customers`);
    } catch (error) {
      console.error('Failed to batch save customers:', error);
    }
  }

  // ==================== 订单数据操作 ====================

  /**
   * 保存订单
   */
  async saveOrder(order: Order): Promise<number> {
    if (!this.rdbStore) return -1;

    try {
      const valueBucket = {
        [OrderColumns.ORDER_NO]: order.orderNo,
        [OrderColumns.CUSTOMER_ID]: order.customerId,
        [OrderColumns.TOTAL_PRICE]: order.totalPrice,
        [OrderColumns.PREPAID]: order.prepaid,
        [OrderColumns.PAY_TYPE]: order.payType,
        [OrderColumns.URGENT]: order.urgent,
        [OrderColumns.STATUS]: order.status,
        [OrderColumns.EXPECTED_TIME]: order.expectedTime,
        [OrderColumns.CREATE_TIME]: order.createTime
      };
      
      // 检查是否已存在
      const existing = await this.getOrderById(order.id);
      
      if (existing) {
        const predicates = new relationalStore.RdbPredicates(Tables.ORDERS);
        predicates.equalTo(OrderColumns.ID, order.id.toString());
        await this.rdbStore.update(valueBucket, predicates);
        console.info(`Order updated: ${order.id}`);
        return order.id;
      } else {
        const id = await this.rdbStore.insert(Tables.ORDERS, valueBucket);
        console.info(`Order inserted: ${id}`);
        return id;
      }
    } catch (error) {
      console.error('Failed to save order:', error);
      return -1;
    }
  }

  /**
   * 获取所有订单
   */
  async getAllOrders(): Promise<Order[]> {
    if (!this.rdbStore) return [];

    try {
      const predicates = new relationalStore.RdbPredicates(Tables.ORDERS);
      const resultSet = await this.rdbStore.query(predicates);
      
      const orders: Order[] = [];
      while (resultSet.goToNextRow()) {
        const order: Order = {
          id: resultSet.getLong(resultSet.getColumnIndex(OrderColumns.ID)),
          orderNo: resultSet.getString(resultSet.getColumnIndex(OrderColumns.ORDER_NO)),
          customerId: resultSet.getLong(resultSet.getColumnIndex(OrderColumns.CUSTOMER_ID)),
          totalPrice: resultSet.getDouble(resultSet.getColumnIndex(OrderColumns.TOTAL_PRICE)),
          prepaid: resultSet.getDouble(resultSet.getColumnIndex(OrderColumns.PREPAID)),
          payType: resultSet.getString(resultSet.getColumnIndex(OrderColumns.PAY_TYPE)),
          urgent: resultSet.getLong(resultSet.getColumnIndex(OrderColumns.URGENT)),
          status: resultSet.getString(resultSet.getColumnIndex(OrderColumns.STATUS)),
          expectedTime: resultSet.getString(resultSet.getColumnIndex(OrderColumns.EXPECTED_TIME)),
          createTime: resultSet.getString(resultSet.getColumnIndex(OrderColumns.CREATE_TIME))
        };
        orders.push(order);
      }
      
      resultSet.close();
      return orders;
    } catch (error) {
      console.error('Failed to get all orders:', error);
      return [];
    }
  }

  /**
   * 根据 ID 获取订单
   */
  async getOrderById(id: number): Promise<Order | null> {
    if (!this.rdbStore) return null;

    try {
      const predicates = new relationalStore.RdbPredicates(Tables.ORDERS);
      predicates.equalTo(OrderColumns.ID, id.toString());
      
      const resultSet = await this.rdbStore.query(predicates);
      
      if (resultSet.goToFirstRow()) {
        const order: Order = {
          id: resultSet.getLong(resultSet.getColumnIndex(OrderColumns.ID)),
          orderNo: resultSet.getString(resultSet.getColumnIndex(OrderColumns.ORDER_NO)),
          customerId: resultSet.getLong(resultSet.getColumnIndex(OrderColumns.CUSTOMER_ID)),
          totalPrice: resultSet.getDouble(resultSet.getColumnIndex(OrderColumns.TOTAL_PRICE)),
          prepaid: resultSet.getDouble(resultSet.getColumnIndex(OrderColumns.PREPAID)),
          payType: resultSet.getString(resultSet.getColumnIndex(OrderColumns.PAY_TYPE)),
          urgent: resultSet.getLong(resultSet.getColumnIndex(OrderColumns.URGENT)),
          status: resultSet.getString(resultSet.getColumnIndex(OrderColumns.STATUS)),
          expectedTime: resultSet.getString(resultSet.getColumnIndex(OrderColumns.EXPECTED_TIME)),
          createTime: resultSet.getString(resultSet.getColumnIndex(OrderColumns.CREATE_TIME))
        };
        resultSet.close();
        return order;
      }
      
      resultSet.close();
      return null;
    } catch (error) {
      console.error('Failed to get order:', error);
      return null;
    }
  }

  // ==================== 网络状态和数据同步 ====================

  /**
   * 设置在线状态
   */
  setOnlineStatus(isOnline: boolean): void {
    this.isOnline = isOnline;
    console.info(`Online status changed: ${isOnline}`);
  }

  /**
   * 获取在线状态
   */
  isOnlineStatus(): boolean {
    return this.isOnline;
  }

  /**
   * 清空所有数据
   */
  async clearAllData(): Promise<void> {
    if (!this.rdbStore) return;

    try {
      await this.rdbStore.executeSql(`DELETE FROM ${Tables.CUSTOMER}`);
      await this.rdbStore.executeSql(`DELETE FROM ${Tables.ORDERS}`);
      await this.rdbStore.executeSql(`DELETE FROM ${Tables.CLOTHES}`);
      await this.rdbStore.executeSql(`DELETE FROM ${Tables.RECHARGE_RECORD}`);
      console.info('All data cleared');
    } catch (error) {
      console.error('Failed to clear data:', error);
    }
  }

  /**
   * 关闭数据库
   */
  async close(): Promise<void> {
    if (this.rdbStore) {
      this.rdbStore.close();
      this.rdbStore = null;
      console.info('Database closed');
    }
  }
}

// 导出单例
export const dbHelper = DatabaseHelper.getInstance();
