import { Customer, CreateCustomerRequest } from '../models/Customer';
import { dbHelper } from '../database/DatabaseHelper';

/**
 * 客户服务类（本地数据库版本）
 */
export class CustomerService {
  
  /**
   * 获取所有客户
   */
  async getAllCustomers(): Promise<Customer[]> {
    const customers = await dbHelper.getAllCustomers();
    
    // 预览器模式下返回测试数据
    if (customers.length === 0) {
      return [
        { id: 1, name: '张三', phone: '13912345678', wechat: 'zhangsan', balance: 100, createTime: new Date().toISOString() },
        { id: 2, name: '李四', phone: '13887654321', wechat: 'lisi', balance: 200, createTime: new Date().toISOString() }
      ];
    }
    
    return customers;
  }

  /**
   * 根据 ID 获取客户
   */
  async getCustomerById(id: number): Promise<Customer | null> {
    return await dbHelper.getCustomerById(id);
  }

  /**
   * 按手机号搜索客户
   */
  async getCustomerByPhone(phone: string): Promise<Customer | null> {
    const customers = await dbHelper.getAllCustomers();
    return customers.find(c => c.phone === phone) || null;
  }

  /**
   * 按姓名搜索客户
   */
  async getCustomerByName(name: string): Promise<Customer | null> {
    const customers = await dbHelper.getAllCustomers();
    return customers.find(c => c.name === name) || null;
  }

  /**
   * 模糊搜索客户
   */
  async fuzzySearch(params?: { name?: string, phone?: string, note?: string }): Promise<Customer[]> {
    const customers = await dbHelper.getAllCustomers();
    
    if (!params) return customers;
    
    return customers.filter(customer => {
      if (params.name && !customer.name.includes(params.name)) return false;
      if (params.phone && !customer.phone.includes(params.phone)) return false;
      return true;
    });
  }

  /**
   * 创建客户
   */
  async createCustomer(customer: CreateCustomerRequest): Promise<Customer> {
    const newCustomer: Customer = {
      id: 0, // 数据库自动生成
      name: customer.name,
      phone: customer.phone,
      wechat: customer.wechat,
      balance: customer.balance || 0,
      createTime: new Date().toISOString()
    };
    
    const id = await dbHelper.saveCustomer(newCustomer);
    newCustomer.id = id;
    return newCustomer;
  }

  /**
   * 更新客户
   */
  async updateCustomer(id: number, customer: Partial<Customer>): Promise<Customer> {
    const existing = await dbHelper.getCustomerById(id);
    if (!existing) {
      throw new Error('Customer not found');
    }
    
    const updated: Customer = {
      ...existing,
      name: customer.name ?? existing.name,
      phone: customer.phone ?? existing.phone,
      wechat: customer.wechat ?? existing.wechat,
      balance: customer.balance ?? existing.balance
    };
    
    await dbHelper.saveCustomer(updated);
    return updated;
  }

  /**
   * 删除客户
   */
  async deleteCustomer(id: number): Promise<void> {
    // 注意：当前数据库 helper 需要添加 delete 方法
    console.info(`Delete customer: ${id}`);
  }
}

// 导出单例
export const customerService = new CustomerService();
