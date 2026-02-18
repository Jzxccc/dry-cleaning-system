import { dbHelper } from '../database/DatabaseHelper';
import { Customer } from '../models/Customer';
import { Order } from '../models/Order';

const LOG_TAG = 'DRY CLEAN SYSTEM LOG:';

/**
 * 数据初始化服务
 */
export class DataInitService {
  private static instance: DataInitService;
  private static readonly DATA_KEY = 'app_data_initialized';

  private constructor() {}

  static getInstance(): DataInitService {
    if (!DataInitService.instance) {
      DataInitService.instance = new DataInitService();
    }
    return DataInitService.instance;
  }

  /**
   * 初始化测试数据（仅首次运行）
   */
  async initTestData(): Promise<void> {
    try {
      console.info(LOG_TAG, 'DataInitService.initTestData started');
      
      // 检查是否已初始化
      const initialized = await this.checkInitialized();
      if (initialized) {
        console.info(LOG_TAG, 'Data already initialized, skip');
        return;
      }

      console.info(LOG_TAG, 'Initializing test data...');

      // 添加测试客户
      const customers: Customer[] = [
        {
          id: 0,
          name: '张三',
          phone: '13912345678',
          wechat: 'zhangsan',
          balance: 500,
          createTime: new Date().toISOString()
        },
        {
          id: 0,
          name: '李四',
          phone: '13887654321',
          wechat: 'lisi',
          balance: 300,
          createTime: new Date().toISOString()
        },
        {
          id: 0,
          name: '王五',
          phone: '13711112222',
          wechat: 'wangwu',
          balance: 1000,
          createTime: new Date().toISOString()
        }
      ];

      for (const customer of customers) {
        const id = await dbHelper.saveCustomer(customer);
        console.info(LOG_TAG, `Added customer: ${customer.name}, ID: ${id}`);
      }
      console.info(LOG_TAG, `Added ${customers.length} customers`);

      // 添加测试订单
      const orders: Order[] = [
        {
          id: 0,
          orderNo: 'ORD-20250218-001',
          customerId: 1,
          totalPrice: 100,
          prepaid: 0,
          payType: 'CASH',
          urgent: 0,
          status: 'UNWASHED',
          expectedTime: '',
          createTime: new Date().toISOString()
        },
        {
          id: 0,
          orderNo: 'ORD-20250218-002',
          customerId: 2,
          totalPrice: 200,
          prepaid: 0,
          payType: 'PREPAID',
          urgent: 0,
          status: 'WASHED',
          expectedTime: '',
          createTime: new Date().toISOString()
        },
        {
          id: 0,
          orderNo: 'ORD-20250218-003',
          customerId: 3,
          totalPrice: 150,
          prepaid: 0,
          payType: 'CASH',
          urgent: 0,
          status: 'FINISHED',
          expectedTime: '',
          createTime: new Date().toISOString()
        }
      ];

      for (const order of orders) {
        const id = await dbHelper.saveOrder(order);
        console.info(LOG_TAG, `Added order: ${order.orderNo}, ID: ${id}`);
      }
      console.info(LOG_TAG, `Added ${orders.length} orders`);

      // 标记已初始化
      await this.setInitialized();

      console.info(LOG_TAG, 'Test data initialization complete');
    } catch (error) {
      console.error(LOG_TAG, 'Failed to init test data:', error);
    }
  }

  /**
   * 检查是否已初始化
   */
  private async checkInitialized(): Promise<boolean> {
    // 通过检查是否有客户数据来判断
    const customers = await dbHelper.getAllCustomers();
    return customers.length > 0;
  }

  /**
   * 标记已初始化
   */
  private async setInitialized(): Promise<void> {
    // 简单实现，实际可以使用 Preferences
    console.info('Marked as initialized');
  }
}

// 导出单例
export const dataInitService = DataInitService.getInstance();
