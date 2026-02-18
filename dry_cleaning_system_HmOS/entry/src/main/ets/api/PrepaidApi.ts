import { RechargeRecord, RechargeRequest } from '../models/RechargeRecord';
import { dbHelper, Tables } from '../database/DatabaseHelper';
import { customerService } from './CustomerApi';

/**
 * 充值服务类（本地数据库版本）
 */
export class PrepaidService {
  
  /**
   * 充值
   */
  async recharge(request: RechargeRequest): Promise<string> {
    // 获取客户
    const customer = await customerService.getCustomerById(request.customerId);
    if (!customer) {
      throw new Error('Customer not found');
    }
    
    // 计算赠送金额
    const giftAmount = request.amount * 0.2;
    const totalAdded = request.amount + giftAmount;
    
    // 更新客户余额
    const newBalance = customer.balance + totalAdded;
    await customerService.updateCustomer(request.customerId, { balance: newBalance });
    
    // 创建充值记录
    const record: RechargeRecord = {
      id: 0,
      customerId: request.customerId,
      rechargeAmount: request.amount,
      giftAmount: giftAmount,
      createTime: new Date().toISOString()
    };
    
    // 保存到数据库
    await this.saveRechargeRecord(record);
    
    return `充值成功！充值：${request.amount}元，赠送：${giftAmount}元，余额：${newBalance}元`;
  }

  /**
   * 获取客户充值记录
   */
  async getRechargeRecords(customerId: number): Promise<RechargeRecord[]> {
    // 从数据库查询
    return await this.getAllRechargeRecords();
  }

  /**
   * 获取所有充值记录
   */
  async getAllRechargeRecords(): Promise<RechargeRecord[]> {
    // 简化处理，返回空数组
    // 实际需要从数据库查询
    return [];
  }

  /**
   * 保存充值记录
   */
  private async saveRechargeRecord(record: RechargeRecord): Promise<void> {
    // 需要添加到数据库
    console.info('Save recharge record:', record);
  }
}

// 导出单例
export const prepaidService = new PrepaidService();
