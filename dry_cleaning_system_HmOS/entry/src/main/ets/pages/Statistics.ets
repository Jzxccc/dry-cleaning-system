import router from '@ohos.router';
import { statisticsService } from '../api/StatisticsApi';
import { orderService } from '../api/OrderApi';
import { customerService } from '../api/CustomerApi';
import { MoneyUtil } from '../utils/MoneyUtil';
import { DateUtil } from '../utils/DateUtil';
import { Order } from '../models/Order';
import { Customer } from '../models/Customer';

const LOG_TAG = 'DRY CLEAN SYSTEM LOG:';

/**
 * DatePicker 结果类型
 */
interface DatePickerResult {
  year: number;
  month: number;
  day: number;
}

/**
 * 统计报表页面
 */
@Entry
@Component
struct Statistics {
  @State selectedDate: string = DateUtil.getToday();
  @State selectedYear: number = new Date().getFullYear();
  @State selectedMonth: number = new Date().getMonth() + 1;
  @State activeTab: string = 'daily';

  // 日报数据
  @State dailyIncome: number = 0;
  @State cashIncome: number = 0;
  @State prepaidIncome: number = 0;
  @State unfinishedCount: number = 0;
  @State dailyOrders: Order[] = [];

  // 月报数据
  @State monthlyIncome: number = 0;
  @State monthlyOrders: Order[] = [];

  @State customers: Map<number, Customer> = new Map();
  @State isLoading: boolean = false;

  // DatePicker 状态
  @State showDatePicker: boolean = false;

  aboutToAppear() {
    this.loadDailyStats();
  }

  async loadCustomers() {
    try {
      const customerList: Customer[] = await customerService.getAllCustomers();
      customerList.forEach(c => {
        this.customers.set(c.id, c);
      });
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load customers:', error);
    }
  }

  async loadDailyStats() {
    try {
      console.info(LOG_TAG, `Loading daily stats for: ${this.selectedDate}`);
      this.isLoading = true;

      // 加载收入统计
      this.dailyIncome = await statisticsService.getDailyIncome(this.selectedDate);
      this.cashIncome = await statisticsService.getCashIncome(this.selectedDate);
      this.prepaidIncome = await statisticsService.getPrepaidIncome(this.selectedDate);
      this.unfinishedCount = await statisticsService.getUnfinishedOrderCount();

      console.info(LOG_TAG, `Daily income: ${this.dailyIncome}, Cash: ${this.cashIncome}, Prepaid: ${this.prepaidIncome}`);

      // 加载订单明细
      await this.loadDailyOrders();

      this.isLoading = false;
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load daily stats:', error);
      this.isLoading = false;
    }
  }

  async loadDailyOrders() {
    try {
      const allOrders = await orderService.getAllOrders();
      this.dailyOrders = allOrders.filter(order => {
        const orderDate = order.createTime ? order.createTime.split('T')[0] : '';
        return orderDate === this.selectedDate;
      });
      console.info(LOG_TAG, `Loaded ${this.dailyOrders.length} orders for ${this.selectedDate}`);
      await this.loadCustomers();
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load daily orders:', error);
    }
  }

  async loadMonthlyStats() {
    try {
      console.info(LOG_TAG, `Loading monthly stats for: ${this.selectedYear}-${this.selectedMonth}`);
      this.isLoading = true;

      // 加载月度收入
      this.monthlyIncome = await statisticsService.getMonthlyIncome(this.selectedYear, this.selectedMonth);

      console.info(LOG_TAG, `Monthly income: ${this.monthlyIncome}`);

      // 加载月度订单
      await this.loadMonthlyOrders();

      this.isLoading = false;
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load monthly stats:', error);
      this.isLoading = false;
    }
  }

  async loadMonthlyOrders() {
    try {
      const allOrders = await orderService.getAllOrders();
      this.monthlyOrders = allOrders.filter(order => {
        if (!order.createTime) return false;
        const orderDate = new Date(order.createTime);
        return orderDate.getFullYear() === this.selectedYear &&
          (orderDate.getMonth() + 1) === this.selectedMonth;
      });
      console.info(LOG_TAG, `Loaded ${this.monthlyOrders.length} orders for ${this.selectedYear}-${this.selectedMonth}`);
      await this.loadCustomers();
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load monthly orders:', error);
    }
  }

  getCustomerName(customerId: number): string {
    const customer = this.customers.get(customerId);
    return customer ? customer.name : '未知客户';
  }

  getStatusText(status: string): string {
    const statusMap: Record<string, string> = {
      'UNWASHED': '未洗',
      'WASHED': '已洗',
      'FINISHED': '已取'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: string): string {
    const colorMap: Record<string, string> = {
      'UNWASHED': '#FF9800',
      'WASHED': '#2196F3',
      'FINISHED': '#4CAF50'
    };
    return colorMap[status] || '#999999';
  }

  getPayTypeText(payType: string): string {
    const payTypeMap: Record<string, string> = {
      'CASH': '现金',
      'PREPAID': '储值',
      'UNPAID': '未支付'
    };
    return payTypeMap[payType] || payType;
  }

  onDateSelected(date: string) {
    this.selectedDate = date;
    console.info(LOG_TAG, `Date selected: ${date}`);
    this.loadDailyStats();
  }

  onYearMonthSelected(year: number, month: number) {
    this.selectedYear = year;
    this.selectedMonth = month;
    console.info(LOG_TAG, `YearMonth selected: ${year}-${month}`);
    this.loadMonthlyStats();
    this.showDatePicker = false;  // 关闭弹窗
  }

  onDateChange(result: DatePickerResult) {
    const year = result.year;
    const month = String(result.month).padStart(2, '0');
    const day = String(result.day).padStart(2, '0');
    const dateStr = `${year}-${month}-${day}`;
    this.selectedDate = dateStr;  // 先更新状态
    this.onDateSelected(dateStr);  // 再加载数据
    this.showDatePicker = false;  // 关闭弹窗
  }

  @Builder
  DatePickerDialog() {
    Column() {
      DatePicker({
        start: new Date(2024, 0, 1),
        end: new Date(),
        selected: this.selectedDate ? new Date(this.selectedDate) : new Date(),
      })
        .onChange((result: DatePickerResult) => {
          this.onDateChange(result);
        })
    }
    .width('90%')
    .backgroundColor('#ffffff')
    .borderRadius(12)
  }

  build() {
    Column() {
      // 顶部导航栏
      Row() {
        Button('←')
          .width(40)
          .height(40)
          .fontSize(20)
          .backgroundColor('transparent')
          .fontColor('#333333')
          .onClick(() => {
            router.back();
          })

        Text('统计报表')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Blank()
          .width(40)
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })
      .backgroundColor('#ffffff')

      // Tab 切换
      Row() {
        Button('日报')
          .flexGrow(1)
          .height(44)
          .fontSize(16)
          .backgroundColor(this.activeTab === 'daily' ? '#007DFF' : '#f5f5f5')
          .fontColor(this.activeTab === 'daily' ? '#ffffff' : '#333333')
          .onClick(() => {
            this.activeTab = 'daily';
            this.loadDailyStats();
          })

        Button('月报')
          .flexGrow(1)
          .height(44)
          .fontSize(16)
          .backgroundColor(this.activeTab === 'monthly' ? '#007DFF' : '#f5f5f5')
          .fontColor(this.activeTab === 'monthly' ? '#ffffff' : '#333333')
          .onClick(() => {
            this.activeTab = 'monthly';
            this.loadMonthlyStats();
          })
      }
      .width('100%')
      .padding({ left: 16, right: 16, bottom: 16 })
      .backgroundColor('#ffffff')

      Divider()

      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .width(48)
            .height(48)
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else {
        Scroll() {
          Column() {
            if (this.activeTab === 'daily') {
              // 日报内容
              this.DailyContent()
            } else {
              // 月报内容
              this.MonthlyContent()
            }
          }
          .width('100%')
        }
        .layoutWeight(1)
        .scrollBar(BarState.Off)
      }

      // DatePicker 弹窗
      if (this.showDatePicker) {
        Column() {
          // 遮罩层
          Column() {
            this.DatePickerDialog()
          }
          .width('100%')
          .height('100%')
          .backgroundColor('rgba(0, 0, 0, 0.5)')
          .justifyContent(FlexAlign.Center)
          .alignItems(HorizontalAlign.Center)
          .onClick(() => {
            this.showDatePicker = false;
          })
        }
        .width('100%')
        .height('100%')
        .position({ x: 0, y: 0 })
        .zIndex(1000)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  DailyContent() {
    Column() {
      // 日期选择器（滚轮）
      Column() {
        Text('选择日期')
          .fontSize(14)
          .fontColor('#666666')
          .margin({ bottom: 8 })

        Row() {
          Text(this.selectedDate)
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .fontColor('#333333')
            .layoutWeight(1)

          Button('选择日期')
            .width(120)
            .height(40)
            .onClick(() => {
              this.showDatePicker = true;
            })
        }
        .width('100%')
        .padding(16)
        .backgroundColor('#ffffff')
        .borderRadius(8)
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')
      .margin({ bottom: 1 })

      // 统计卡片
      Grid() {
        GridItem() {
          this.StatCard('今日收入', MoneyUtil.format(this.dailyIncome), '#007DFF')
        }
        GridItem() {
          this.StatCard('现金收入', MoneyUtil.format(this.cashIncome), '#4CAF50')
        }
        GridItem() {
          this.StatCard('储值收入', MoneyUtil.format(this.prepaidIncome), '#FF9800')
        }
        GridItem() {
          this.StatCard('未完成订单', this.unfinishedCount.toString(), '#F44336')
        }
      }
      .columnsTemplate('1fr 1fr')
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')
      .margin({ bottom: 1 })

      // 订单明细
      Column() {
        Row() {
          Text(`订单明细 (${this.dailyOrders.length}笔)`)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
        }
        .width('100%')
        .padding(16)
        .backgroundColor('#ffffff')

        if (this.dailyOrders.length === 0) {
          Text('暂无订单')
            .fontSize(14)
            .fontColor('#999999')
            .width('100%')
            .textAlign(TextAlign.Center)
            .padding(20)
        } else {
          List() {
            ForEach(this.dailyOrders, (order: Order) => {
              ListItem() {
                Column() {
                  Row() {
                    Text(order.orderNo)
                      .fontSize(14)
                      .fontWeight(FontWeight.Medium)

                    Blank()

                    Text(this.getStatusText(order.status))
                      .fontSize(12)
                      .fontColor('#ffffff')
                      .padding({ left: 8, right: 8, top: 4, bottom: 4 })
                      .borderRadius(4)
                      .backgroundColor(this.getStatusColor(order.status))
                  }
                  .width('100%')

                  Row() {
                    Text(this.getCustomerName(order.customerId))
                      .fontSize(13)
                      .fontColor('#666666')

                    Blank()

                    Text(MoneyUtil.format(order.totalPrice))
                      .fontSize(14)
                      .fontWeight(FontWeight.Bold)
                      .fontColor('#007DFF')
                  }
                  .width('100%')
                  .margin({ top: 4 })

                  Row() {
                    Text(this.getPayTypeText(order.payType))
                      .fontSize(12)
                      .fontColor('#999999')

                    Blank()

                    Text(order.createTime ? order.createTime.substring(0, 16) : '')
                      .fontSize(12)
                      .fontColor('#999999')
                  }
                  .width('100%')
                  .margin({ top: 4 })
                }
                .width('100%')
                .padding(12)
              }
            })
          }
          .width('100%')
        }
      }
      .width('100%')
      .backgroundColor('#ffffff')
      .margin({ bottom: 16 })
    }
    .width('100%')
  }

  @Builder
  MonthlyContent() {
    Column() {
      // 年月选择器（滚轮）
      Column() {
        Text('选择月份')
          .fontSize(14)
          .fontColor('#666666')
          .margin({ bottom: 8 })

        Row() {
          Text(`${this.selectedYear}年${this.selectedMonth}月`)
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .fontColor('#333333')
            .layoutWeight(1)

          Button('选择月份')
            .width(120)
            .height(40)
            .onClick(() => {
              this.showDatePicker = true;
            })
        }
        .width('100%')
        .padding(16)
        .backgroundColor('#ffffff')
        .borderRadius(8)
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')
      .margin({ bottom: 1 })

      // 月度收入卡片
      Column() {
        Text('月度收入')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 12 })

        Text(MoneyUtil.format(this.monthlyIncome))
          .fontSize(32)
          .fontWeight(FontWeight.Bold)
          .fontColor('#007DFF')
      }
      .width('100%')
      .padding(24)
      .backgroundColor('#ffffff')
      .borderRadius(8)
      .margin({ bottom: 1 })

      // 月度订单明细
      Column() {
        Row() {
          Text(`本月订单 (${this.monthlyOrders.length}笔)`)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
        }
        .width('100%')
        .padding(16)
        .backgroundColor('#ffffff')

        if (this.monthlyOrders.length === 0) {
          Text('暂无订单')
            .fontSize(14)
            .fontColor('#999999')
            .width('100%')
            .textAlign(TextAlign.Center)
            .padding(20)
        } else {
          List() {
            ForEach(this.monthlyOrders, (order: Order) => {
              ListItem() {
                Column() {
                  Row() {
                    Text(order.orderNo)
                      .fontSize(14)
                      .fontWeight(FontWeight.Medium)

                    Blank()

                    Text(this.getStatusText(order.status))
                      .fontSize(12)
                      .fontColor('#ffffff')
                      .padding({ left: 8, right: 8, top: 4, bottom: 4 })
                      .borderRadius(4)
                      .backgroundColor(this.getStatusColor(order.status))
                  }
                  .width('100%')

                  Row() {
                    Text(this.getCustomerName(order.customerId))
                      .fontSize(13)
                      .fontColor('#666666')

                    Blank()

                    Text(MoneyUtil.format(order.totalPrice))
                      .fontSize(14)
                      .fontWeight(FontWeight.Bold)
                      .fontColor('#007DFF')
                  }
                  .width('100%')
                  .margin({ top: 4 })

                  Row() {
                    Text(this.getPayTypeText(order.payType))
                      .fontSize(12)
                      .fontColor('#999999')

                    Blank()

                    Text(order.createTime ? order.createTime.substring(0, 16) : '')
                      .fontSize(12)
                      .fontColor('#999999')
                  }
                  .width('100%')
                  .margin({ top: 4 })
                }
                .width('100%')
                .padding(12)
              }
            })
          }
          .width('100%')
        }
      }
      .width('100%')
      .backgroundColor('#ffffff')
      .margin({ bottom: 16 })
    }
    .width('100%')
  }

  @Builder
  StatCard(title: string, value: string, color: string) {
    Column() {
      Text(value)
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
        .fontColor(color)
        .margin({ bottom: 4 })

      Text(title)
        .fontSize(14)
        .fontColor('#666666')
    }
    .width('100%')
    .padding(16)
    .backgroundColor('#f9f9f9')
    .borderRadius(8)
    .alignItems(HorizontalAlign.Center)
  }
}
