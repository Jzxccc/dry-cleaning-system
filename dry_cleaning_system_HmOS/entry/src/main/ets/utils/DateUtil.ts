/**
 * 日期格式化工具
 */
export class DateUtil {
  
  /**
   * 格式化为 YYYY-MM-DD
   */
  static formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  
  /**
   * 格式化为 YYYY-MM-DD HH:mm:ss
   */
  static formatDateTime(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
  }
  
  /**
   * 解析日期字符串
   */
  static parseDate(dateStr: string): Date {
    return new Date(dateStr);
  }
  
  /**
   * 获取今天日期
   */
  static getToday(): string {
    return this.formatDate(new Date());
  }
  
  /**
   * 获取本月月份
   */
  static getCurrentMonth(): { year: number, month: number } {
    const date = new Date();
    return {
      year: date.getFullYear(),
      month: date.getMonth() + 1
    };
  }
}
