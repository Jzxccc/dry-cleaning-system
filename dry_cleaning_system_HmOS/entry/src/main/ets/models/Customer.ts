/**
 * 客户数据模型
 */
export interface Customer {
  id: number;
  name: string;
  phone: string;
  wechat: string;
  balance: number;
  createTime: string;
}

/**
 * 创建客户请求
 */
export interface CreateCustomerRequest {
  name: string;
  phone: string;
  wechat?: string;
  balance?: number;
}

/**
 * 更新客户请求
 */
export interface UpdateCustomerRequest {
  name?: string;
  phone?: string;
  wechat?: string;
  balance?: number;
}
