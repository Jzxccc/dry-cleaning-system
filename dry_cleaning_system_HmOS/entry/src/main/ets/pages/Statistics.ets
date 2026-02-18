import router from '@ohos.router';
import { statisticsService } from '../api/StatisticsApi';
import { orderService } from '../api/OrderApi';
import { customerService } from '../api/CustomerApi';
import { MoneyUtil } from '../utils/MoneyUtil';
import { DateUtil } from '../utils/DateUtil';
import { Order } from '../models/Order';
import { Customer } from '../models/Customer';

/**
 * 统计报表页面
 */
@Entry
@Component
struct Statistics {
  @State selectedDate: string = DateUtil.getToday();
  @State selectedYear: number = new Date().getFullYear();
  @State selectedMonth: number = new Date().getMonth() + 1;
  @State activeTab: string = 'daily'; // 'daily' or 'monthly'
  
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
      console.error('Failed to load customers:', error);
    }
  }

  async loadDailyStats() {
    try {
      this.isLoading = true;
      
      // 加载收入统计
      this.dailyIncome = await statisticsService.getDailyIncome(this.selectedDate);
      this.cashIncome = await statisticsService.getCashIncome(this.selectedDate);
      this.prepaidIncome = await statisticsService.getPrepaidIncome(this.selectedDate);
      this.unfinishedCount = await statisticsService.getUnfinishedOrderCount();
      
      // 加载订单明细
      await this.loadDailyOrders();
      
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to load daily stats:', error);
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
      await this.loadCustomers();
    } catch (error) {
      console.error('Failed to load daily orders:', error);
    }
  }

  async loadMonthlyStats() {
    try {
      this.isLoading = true;
      
      // 加载月度收入
      this.monthlyIncome = await statisticsService.getMonthlyIncome(this.selectedYear, this.selectedMonth);
      
      // 加载月度订单
      await this.loadMonthlyOrders();
      
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to load monthly stats:', error);
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
      await this.loadCustomers();
    } catch (error) {
      console.error('Failed to load monthly orders:', error);
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
        // 内容区域
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
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  DailyContent() {
    Column() {
      // 日期选择器
      Row() {
        Text('选择日期：')
          .fontSize(16)
          .fontColor('#333333')

        TextInput({ text: this.selectedDate })
          .layoutWeight(1)
          .height(40)
          .backgroundColor('#ffffff')
          .borderRadius(8)
          .padding({ left: 16, right: 16 })

        Button('查询')
          .width(80)
          .height(40)
          .margin({ left: 8 })
          .onClick(() => {
            this.loadDailyStats();
          })
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')

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
      .margin({ top: 1 })

      // 订单明细
      Column() {
        Row() {
          Text('订单明细')
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
                      .padding({ left: 8, right: 8, top: 2, bottom: 2 })
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
      .margin({ top: 1 })
    }
    .width('100%')
  }

  @Builder
  MonthlyContent() {
    Column() {
      // 月份选择器
      Row() {
        Text('年份：')
          .fontSize(16)
          .fontColor('#333333')

        TextInput({ text: this.selectedYear.toString() })
          .width(100)
          .height(40)
          .backgroundColor('#ffffff')
          .borderRadius(8)
          .padding({ left: 16, right: 16 })
          .type(InputType.Number)

        Text('月份：')
          .fontSize(16)
          .fontColor('#333333')
          .margin({ left: 16 })

        TextInput({ text: this.selectedMonth.toString() })
          .width(60)
          .height(40)
          .backgroundColor('#ffffff')
          .borderRadius(8)
          .padding({ left: 16, right: 16 })
          .type(InputType.Number)

        Button('查询')
          .width(80)
          .height(40)
          .margin({ left: 8 })
          .onClick(() => {
            this.loadMonthlyStats();
          })
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')

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
      .alignItems(HorizontalAlign.Center)
      .margin({ top: 1 })

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
                      .padding({ left: 8, right: 8, top: 2, bottom: 2 })
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
      .margin({ top: 1 })
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
