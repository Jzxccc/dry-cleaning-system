/**
 * 全局事件工具类
 */
export class EventBus {
  private static instance: EventBus;
  public listeners: Map<string, Array<() => void>> = new Map();

  private constructor() {}

  static getInstance(): EventBus {
    if (!EventBus.instance) {
      EventBus.instance = new EventBus();
    }
    return EventBus.instance;
  }

  /**
   * 订阅事件
   */
  on(event: string, callback: () => void): void {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, []);
    }
    this.listeners.get(event)!.push(callback);
  }

  /**
   * 取消订阅
   */
  off(event: string, callback: () => void): void {
    if (this.listeners.has(event)) {
      const callbacks = this.listeners.get(event)!;
      const index = callbacks.indexOf(callback);
      if (index > -1) {
        callbacks.splice(index, 1);
      }
    }
  }

  /**
   * 发布事件
   */
  emit(event: string): void {
    console.info('EventBus', `Emitting event: ${event}`);
    if (this.listeners.has(event)) {
      const callbacks = this.listeners.get(event)!;
      console.info('EventBus', `Found ${callbacks.length} listeners for event: ${event}`);
      callbacks.forEach((callback, index) => {
        try {
          console.info('EventBus', `Calling listener ${index + 1} for event: ${event}`);
          callback();
        } catch (error) {
          console.error('EventBus emit error:', error);
        }
      });
    } else {
      console.warn('EventBus', `No listeners for event: ${event}`);
    }
  }

  /**
   * 清空所有事件
   */
  clear(): void {
    this.listeners.clear();
  }
}

// 导出单例
export const eventBus = EventBus.getInstance();

// 定义事件常量
export const AppEvents = {
  DATA_CHANGED: 'data_changed',  // 数据变化事件
  CUSTOMER_ADDED: 'customer_added',  // 客户添加
  CUSTOMER_DELETED: 'customer_deleted',  // 客户删除
  ORDER_ADDED: 'order_added',  // 订单添加
};
