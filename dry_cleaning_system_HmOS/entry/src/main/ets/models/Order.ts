/**
 * 订单数据模型
 */
export interface Order {
  id: number;
  orderNo: string;
  customerId: number;
  totalPrice: number;
  prepaid: number;
  payType: string;
  urgent: number;
  status: string;
  expectedTime: string;
  createTime: string;
}

/**
 * 创建订单请求
 */
export interface CreateOrderRequest {
  orderNo: string;
  customerId: number;
  totalPrice: number;
  prepaid?: number;
  payType: string;
  urgent?: number;
  status?: string;
  expectedTime?: string;
  createTime?: string;
}

/**
 * 衣物数据模型
 */
export interface Clothes {
  id: number;
  orderId: string;
  type: string;
  price: number;
  damageRemark: string;
  damageImage: string;
  status: string;
  createTime: string;
}

/**
 * 创建衣物请求
 */
export interface CreateClothesRequest {
  orderId: string;
  type: string;
  price: number;
  damageRemark?: string;
  damageImage?: string;
  status?: string;
}
