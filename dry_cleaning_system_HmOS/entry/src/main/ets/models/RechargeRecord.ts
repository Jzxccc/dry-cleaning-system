/**
 * 充值记录数据模型
 */
export interface RechargeRecord {
  id: number;
  customerId: number;
  rechargeAmount: number;
  giftAmount: number;
  createTime: string;
}

/**
 * 充值请求
 */
export interface RechargeRequest {
  customerId: number;
  amount: number;
}
