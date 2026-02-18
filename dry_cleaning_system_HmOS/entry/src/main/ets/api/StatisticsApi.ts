import { dbHelper, Tables, OrderColumns } from '../database/DatabaseHelper';

/**
 * 统计服务类（本地数据库版本）
 */
export class StatisticsService {
  
  /**
   * 获取日收入
   */
  async getDailyIncome(date: string): Promise<number> {
    const orders = await dbHelper.getAllOrders();
    const targetDate = date.split('T')[0];
    
    return orders
      .filter(order => {
        const orderDate = order.createTime.split('T')[0];
        return orderDate === targetDate;
      })
      .reduce((sum, order) => sum + order.totalPrice, 0);
  }

  /**
   * 获取月收入
   */
  async getMonthlyIncome(year: number, month: number): Promise<number> {
    const orders = await dbHelper.getAllOrders();
    
    return orders
      .filter(order => {
        const orderDate = new Date(order.createTime);
        return orderDate.getFullYear() === year && 
               (orderDate.getMonth() + 1) === month;
      })
      .reduce((sum, order) => sum + order.totalPrice, 0);
  }

  /**
   * 获取现金收入
   */
  async getCashIncome(date: string): Promise<number> {
    const orders = await dbHelper.getAllOrders();
    const targetDate = date.split('T')[0];
    
    return orders
      .filter(order => {
        const orderDate = order.createTime.split('T')[0];
        return orderDate === targetDate && order.payType === 'CASH';
      })
      .reduce((sum, order) => sum + order.totalPrice, 0);
  }

  /**
   * 获取储值收入（充值金额）
   */
  async getPrepaidIncome(date: string): Promise<number> {
    // 需要从充值记录表查询，简化处理返回 0
    return 0;
  }

  /**
   * 获取未完成订单数
   */
  async getUnfinishedOrderCount(): Promise<number> {
    const orders = await dbHelper.getAllOrders();
    return orders.filter(order => order.status !== 'FINISHED').length;
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
