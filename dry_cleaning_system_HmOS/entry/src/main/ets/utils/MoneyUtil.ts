/**
 * 金额格式化工具
 */
export class MoneyUtil {
  
  /**
   * 格式化为货币显示
   */
  static format(amount: number): string {
    return `¥${amount.toFixed(2)}`;
  }
  
  /**
   * 格式化整数金额
   */
  static formatInt(amount: number): string {
    return `¥${amount.toFixed(0)}`;
  }
  
  /**
   * 计算赠送金额（充 100 送 20%）
   */
  static calculateGift(amount: number): number {
    if (amount <= 0) return 0;
    return amount * 0.2;
  }
  
  /**
   * 计算实际到账金额
   */
  static calculateTotal(amount: number): number {
    return amount + this.calculateGift(amount);
  }
  
  /**
   * 验证充值金额是否为 100 的整数倍
   */
  static isValidRechargeAmount(amount: number): boolean {
    return amount > 0 && amount % 100 === 0;
  }
}
