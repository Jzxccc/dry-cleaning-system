import { dbHelper, Tables, OrderColumns } from '../database/DatabaseHelper';

const LOG_TAG = 'DRY CLEAN SYSTEM LOG:';

/**
 * 统计服务类（本地数据库版本）
 */
export class StatisticsService {
  
  /**
   * 获取日收入（现金收入 + 充值收入）
   */
  async getDailyIncome(date: string): Promise<number> {
    try {
      const orders = await dbHelper.getAllOrders();
      const targetDate = date.split('T')[0];

      // 现金收入
      const cashIncome = orders
        .filter(order => {
          const orderDate = order.createTime ? order.createTime.split('T')[0] : '';
          return orderDate === targetDate && order.payType === 'CASH';
        })
        .reduce((sum, order) => sum + order.totalPrice, 0);

      // 充值收入
      const rechargeIncome = await dbHelper.getRechargeRecordsByDate(date);

      const totalIncome = cashIncome + rechargeIncome;
      console.info(LOG_TAG, `getDailyIncome(${date}): ${totalIncome} (cash: ${cashIncome}, recharge: ${rechargeIncome})`);
      return totalIncome;
    } catch (error) {
      console.error(LOG_TAG, 'getDailyIncome error:', error);
      return 0;
    }
  }

  /**
   * 获取月收入
   */
  async getMonthlyIncome(year: number, month: number): Promise<number> {
    try {
      const orders = await dbHelper.getAllOrders();
      
      const income = orders
        .filter(order => {
          const orderDate = new Date(order.createTime);
          return orderDate.getFullYear() === year && 
                 (orderDate.getMonth() + 1) === month;
        })
        .reduce((sum, order) => sum + order.totalPrice, 0);
      
      console.info(LOG_TAG, `getMonthlyIncome(${year}-${month}): ${income}`);
      return income;
    } catch (error) {
      console.error(LOG_TAG, 'getMonthlyIncome error:', error);
      return 0;
    }
  }

  /**
   * 获取现金收入
   */
  async getCashIncome(date: string): Promise<number> {
    try {
      const orders = await dbHelper.getAllOrders();
      const targetDate = date.split('T')[0];
      
      const income = orders
        .filter(order => {
          const orderDate = order.createTime ? order.createTime.split('T')[0] : '';
          return orderDate === targetDate && order.payType === 'CASH';
        })
        .reduce((sum, order) => sum + order.totalPrice, 0);
      
      console.info(LOG_TAG, `getCashIncome(${date}): ${income}`);
      return income;
    } catch (error) {
      console.error(LOG_TAG, 'getCashIncome error:', error);
      return 0;
    }
  }

  /**
   * 获取储值收入（充值金额）
   */
  async getPrepaidIncome(date: string): Promise<number> {
    // TODO: 需要从充值记录表查询
    console.info(LOG_TAG, `getPrepaidIncome(${date}): 0`);
    return 0;
  }

  /**
   * 获取今日订单数
   */
  async getTodayOrderCount(date: string): Promise<number> {
    try {
      const orders = await dbHelper.getAllOrders();
      const targetDate = date.split('T')[0];

      const count = orders.filter(order => {
        const orderDate = order.createTime ? order.createTime.split('T')[0] : '';
        return orderDate === targetDate;
      }).length;

      console.info(LOG_TAG, `getTodayOrderCount(${date}): ${count}`);
      return count;
    } catch (error) {
      console.error(LOG_TAG, 'getTodayOrderCount error:', error);
      return 0;
    }
  }

  /**
   * 获取未完成订单数
   */
  async getUnfinishedOrderCount(): Promise<number> {
    try {
      const orders = await dbHelper.getAllOrders();
      const count = orders.filter(order => order.status !== 'FINISHED').length;
      console.info(LOG_TAG, `getUnfinishedOrderCount: ${count}`);
      return count;
    } catch (error) {
      console.error(LOG_TAG, 'getUnfinishedOrderCount error:', error);
      return 0;
    }
  }

  /**
   * 获取每日统计
   */
  async getDailyStatistics(date: string): Promise<Record<string, any>> {
    return {
      dailyIncome: await this.getDailyIncome(date),
      cashIncome: await this.getCashIncome(date),
      prepaidIncome: await this.getPrepaidIncome(date),
      date: date
    };
  }

  /**
   * 获取月度统计
   */
  async getMonthlyStatistics(year: number, month: number): Promise<Record<string, any>> {
    return {
      monthlyIncome: await this.getMonthlyIncome(year, month),
      year: year,
      month: month
    };
  }
}

// 导出单例
export const statisticsService = new StatisticsService();
