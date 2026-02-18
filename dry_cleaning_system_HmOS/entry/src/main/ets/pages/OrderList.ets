import router from '@ohos.router';
import { Order } from '../models/Order';
import { orderService } from '../api/OrderApi';
import { customerService } from '../api/CustomerApi';
import { Customer } from '../models/Customer';
import { MoneyUtil } from '../utils/MoneyUtil';
import { DateUtil } from '../utils/DateUtil';

/**
 * 订单状态类
 */
class OrderStatusItem {
  static UNWASHED: string = 'UNWASHED';
  static WASHED: string = 'WASHED';
  static FINISHED: string = 'FINISHED';
}

/**
 * 订单列表页面
 */
@Entry
@Component
struct OrderList {
  @State orders: Order[] = [];
  @State customers: Map<number, Customer> = new Map();
  @State isLoading: boolean = true;
  @State searchKeyword: string = '';
  @State selectedStatus: string = ''; // 空表示全部

  aboutToAppear() {
    this.loadOrders();
    this.loadCustomers();
  }

  async loadOrders() {
    try {
      this.isLoading = true;
      if (this.selectedStatus) {
        this.orders = await orderService.getOrdersByStatus(this.selectedStatus);
      } else if (this.searchKeyword) {
        this.orders = await orderService.fuzzySearch({ customerName: this.searchKeyword });
      } else {
        this.orders = await orderService.getAllOrders();
      }
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to load orders:', error);
      this.isLoading = false;
    }
  }

  async loadCustomers() {
    try {
      const customerList = await customerService.getAllCustomers();
      customerList.forEach(c => {
        this.customers.set(c.id, c);
      });
    } catch (error) {
      console.error('Failed to load customers:', error);
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

        Text('订单管理')
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

      // 搜索框
      Row() {
        TextInput({ placeholder: '搜索订单号或客户姓名', text: this.searchKeyword })
          .layoutWeight(1)
          .height(40)
          .backgroundColor('#f5f5f5')
          .borderRadius(8)
          .padding({ left: 16, right: 16 })
          .onChange((value) => {
            this.searchKeyword = value;
          })

        Button('搜索')
          .width(80)
          .height(40)
          .margin({ left: 8 })
          .onClick(() => {
            this.loadOrders();
          })
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')

      // 状态筛选
      Row() {
        Button('全部')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === '' ? '#007DFF' : '#f0f0f0')
          .fontColor(this.selectedStatus === '' ? '#ffffff' : '#333333')
          .margin({ right: 4 })
          .onClick(() => {
            this.selectedStatus = '';
            this.loadOrders();
          })

        Button('未洗')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === 'UNWASHED' ? '#FF9800' : '#f0f0f0')
          .fontColor(this.selectedStatus === 'UNWASHED' ? '#ffffff' : '#333333')
          .margin({ right: 4, left: 4 })
          .onClick(() => {
            this.selectedStatus = 'UNWASHED';
            this.loadOrders();
          })

        Button('已洗')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === 'WASHED' ? '#2196F3' : '#f0f0f0')
          .fontColor(this.selectedStatus === 'WASHED' ? '#ffffff' : '#333333')
          .margin({ right: 4, left: 4 })
          .onClick(() => {
            this.selectedStatus = 'WASHED';
            this.loadOrders();
          })

        Button('已取')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === 'FINISHED' ? '#4CAF50' : '#f0f0f0')
          .fontColor(this.selectedStatus === 'FINISHED' ? '#ffffff' : '#333333')
          .margin({ left: 4 })
          .onClick(() => {
            this.selectedStatus = 'FINISHED';
            this.loadOrders();
          })
      }
      .width('100%')
      .padding({ left: 16, right: 16, bottom: 16 })
      .backgroundColor('#ffffff')

      Divider()

      // 订单列表
      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .width(48)
            .height(48)
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else if (this.orders.length === 0) {
        Column() {
          Text('暂无订单')
            .fontSize(16)
            .fontColor('#999999')
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else {
        List() {
          ForEach(this.orders, (order: Order) => {
            ListItem() {
              Column() {
                // 第一行：订单号和状态
                Row() {
                  Text(order.orderNo)
                    .fontSize(16)
                    .fontWeight(FontWeight.Medium)

                  Blank()

                  Text(this.getStatusText(order.status))
                    .fontSize(14)
                    .fontColor('#ffffff')
                    .padding({ left: 12, right: 12, top: 4, bottom: 4 })
                    .borderRadius(4)
                    .backgroundColor(this.getStatusColor(order.status))
                }
                .width('100%')

                // 第二行：客户姓名
                Row() {
                  Text(this.getCustomerName(order.customerId))
                    .fontSize(14)
                    .fontColor('#666666')
                    .margin({ top: 8 })
                }
                .width('100%')

                // 第三行：金额和时间
                Row() {
                  Text(MoneyUtil.format(order.totalPrice))
                    .fontSize(16)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#007DFF')

                  Text(DateUtil.formatDate(DateUtil.parseDate(order.createTime)))
                    .fontSize(12)
                    .fontColor('#999999')
                    .margin({ top: 4 })
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
              }
              .width('100%')
              .padding(16)
              .onClick(() => {
                router.pushUrl({
                  url: 'pages/OrderDetail',
                  params: { orderId: order.id.toString() }
                });
              })
            }
          })
        }
        .width('100%')
        .layoutWeight(1)
        .divider({ strokeWidth: 1, color: '#f0f0f0', startMargin: 16, endMargin: 16 })
      }

      // 新建订单按钮
      Button('+ 新建订单')
        .width('90%')
        .height(56)
        .fontSize(18)
        .margin({ top: 16, bottom: 16 })
        .onClick(() => {
          router.pushUrl({ url: 'pages/NewOrder' });
        })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }
}
