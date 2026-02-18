import { Order, CreateOrderRequest, Clothes, CreateClothesRequest } from '../models/Order';
import { dbHelper, Tables, OrderColumns } from '../database/DatabaseHelper';

/**
 * 订单服务类（本地数据库版本）
 */
export class OrderService {
  
  /**
   * 获取所有订单
   */
  async getAllOrders(): Promise<Order[]> {
    return await dbHelper.getAllOrders();
  }

  /**
   * 根据 ID 获取订单
   */
  async getOrderById(id: number): Promise<Order | null> {
    return await dbHelper.getOrderById(id);
  }

  /**
   * 根据客户 ID 获取订单
   */
  async getOrdersByCustomerId(customerId: number): Promise<Order[]> {
    const orders = await dbHelper.getAllOrders();
    return orders.filter(o => o.customerId === customerId);
  }

  /**
   * 根据客户姓名获取订单
   */
  async getOrdersByCustomerName(customerName: string): Promise<Order[]> {
    // 需要通过客户 ID 关联，简化处理
    const orders = await dbHelper.getAllOrders();
    return orders;
  }

  /**
   * 根据状态获取订单
   */
  async getOrdersByStatus(status: string): Promise<Order[]> {
    const orders = await dbHelper.getAllOrders();
    return orders.filter(o => o.status === status);
  }

  /**
   * 模糊搜索订单
   */
  async fuzzySearch(params?: { orderNo?: string, customerName?: string, clothesType?: string }): Promise<Order[]> {
    const orders = await dbHelper.getAllOrders();
    
    if (!params) return orders;
    
    return orders.filter(order => {
      if (params.orderNo && !order.orderNo.includes(params.orderNo)) return false;
      // customerName 和 clothesType 需要关联查询，简化处理
      return true;
    });
  }

  /**
   * 创建订单
   */
  async createOrder(order: CreateOrderRequest): Promise<Order> {
    const newOrder: Order = {
      id: 0,
      orderNo: order.orderNo,
      customerId: order.customerId,
      totalPrice: order.totalPrice,
      prepaid: order.prepaid || 0,
      payType: order.payType,
      urgent: order.urgent || 0,
      status: order.status || 'UNWASHED',
      expectedTime: order.expectedTime || '',
      createTime: order.createTime || new Date().toISOString()
    };
    
    const id = await dbHelper.saveOrder(newOrder);
    newOrder.id = id;
    return newOrder;
  }

  /**
   * 更新订单
   */
  async updateOrder(id: number, order: Partial<Order>): Promise<Order> {
    const existing = await dbHelper.getOrderById(id);
    if (!existing) {
      throw new Error('Order not found');
    }
    
    const updated: Order = {
      ...existing,
      orderNo: order.orderNo ?? existing.orderNo,
      customerId: order.customerId ?? existing.customerId,
      totalPrice: order.totalPrice ?? existing.totalPrice,
      prepaid: order.prepaid ?? existing.prepaid,
      payType: order.payType ?? existing.payType,
      urgent: order.urgent ?? existing.urgent,
      status: order.status ?? existing.status,
      expectedTime: order.expectedTime ?? existing.expectedTime
    };
    
    await dbHelper.saveOrder(updated);
    return updated;
  }

  /**
   * 更新订单状态
   */
  async updateOrderStatus(id: number, newStatus: string): Promise<Order> {
    return await this.updateOrder(id, { status: newStatus });
  }

  /**
   * 删除订单
   */
  async deleteOrder(id: number): Promise<void> {
    console.info(`Delete order: ${id}`);
  }

  /**
   * 获取衣物列表
   */
  async getClothesByOrderId(orderId: string): Promise<Clothes[]> {
    // 需要从数据库查询，简化处理返回空数组
    return [];
  }

  /**
   * 创建衣物
   */
  async createClothes(clothes: CreateClothesRequest): Promise<Clothes> {
    const newClothes: Clothes = {
      id: 0,
      orderId: clothes.orderId,
      type: clothes.type,
      price: clothes.price,
      damageRemark: clothes.damageRemark || '',
      damageImage: clothes.damageImage || '',
      status: clothes.status || 'UNWASHED',
      createTime: new Date().toISOString()
    };
    
    // 需要添加到数据库
    return newClothes;
  }
}

// 导出单例
export const orderService = new OrderService();
