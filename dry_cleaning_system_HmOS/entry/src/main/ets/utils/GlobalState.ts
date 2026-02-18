import { Customer } from '../models/Customer';

/**
 * 全局状态管理
 */
class GlobalState {
  private static instance: GlobalState;
  private selectedCustomer: Customer | null = null;

  private constructor() {}

  static getInstance(): GlobalState {
    if (!GlobalState.instance) {
      GlobalState.instance = new GlobalState();
    }
    return GlobalState.instance;
  }

  setSelectedCustomer(customer: Customer): void {
    this.selectedCustomer = customer;
  }

  getSelectedCustomer(): Customer | null {
    return this.selectedCustomer;
  }

  clearSelectedCustomer(): void {
    this.selectedCustomer = null;
  }
}

export const globalState = GlobalState.getInstance();
