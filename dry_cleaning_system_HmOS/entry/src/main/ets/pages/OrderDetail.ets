import router from '@ohos.router';
import { promptAction } from '@kit.ArkUI';
import { Order } from '../models/Order';
import { Clothes } from '../models/Order';
import { Customer } from '../models/Customer';
import { orderService } from '../api/OrderApi';
import { customerService } from '../api/CustomerApi';
import { MoneyUtil } from '../utils/MoneyUtil';
import { DateUtil } from '../utils/DateUtil';

/**
 * 订单详情页面
 */
@Entry
@Component
struct OrderDetail {
  @State order: Order | null = null;
  @State customer: Customer | null = null;
  @State clothesList: Clothes[] = [];
  @State isLoading: boolean = true;
  @State orderId: number = 0;

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>;
    if (params && params['orderId']) {
      this.orderId = parseInt(params['orderId']);
      this.loadOrderDetail();
    }
  }

  async loadOrderDetail() {
    try {
      this.isLoading = true;
      
      // 加载订单详情
      this.order = await orderService.getOrderById(this.orderId);
      
      // 加载客户信息
      if (this.order) {
        this.customer = await customerService.getCustomerById(this.order.customerId);
      }
      
      // 加载衣物列表
      if (this.order) {
        this.clothesList = await orderService.getClothesByOrderId(this.order.orderNo);
      }
      
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to load order detail:', error);
      this.isLoading = false;
      promptAction.showToast({ message: '加载订单详情失败' });
    }
  }

  async updateOrderStatus(newStatus: string) {
    if (!this.order) return;

    try {
      this.isLoading = true;
      await orderService.updateOrderStatus(this.order.id, newStatus);
      this.order.status = newStatus;
      this.isLoading = false;
      promptAction.showToast({ message: '订单状态已更新' });
    } catch (error) {
      console.error('Failed to update order status:', error);
      this.isLoading = false;
      promptAction.showToast({ message: '更新失败，请重试' });
    }
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

        Text('订单详情')
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
      } else if (this.order) {
        Scroll() {
          Column() {
            // 订单状态
            Column() {
              Row() {
                Text(this.order.orderNo)
                  .fontSize(18)
                  .fontWeight(FontWeight.Bold)

                Blank()

                Text(this.getStatusText(this.order.status))
                  .fontSize(14)
                  .fontColor('#ffffff')
                  .padding({ left: 12, right: 12, top: 6, bottom: 6 })
                  .borderRadius(4)
                  .backgroundColor(this.getStatusColor(this.order.status))
              }
              .width('100%')

              Row() {
                Text(`创建时间：${DateUtil.formatDate(DateUtil.parseDate(this.order.createTime))}`)
                  .fontSize(14)
                  .fontColor('#666666')
                  .margin({ top: 8 })
              }
              .width('100%')
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#ffffff')

            // 客户信息
            if (this.customer) {
              Column() {
                Row() {
                  Text('客户信息')
                    .fontSize(16)
                    .fontWeight(FontWeight.Medium)
                }
                .width('100%')
                .margin({ bottom: 12 })

                Row() {
                  Text('姓名：')
                    .fontSize(14)
                    .fontColor('#666666')
                  Text(this.customer.name)
                    .fontSize(14)
                    .fontColor('#333333')
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
                .margin({ bottom: 8 })

                Row() {
                  Text('手机号：')
                    .fontSize(14)
                    .fontColor('#666666')
                  Text(this.customer.phone || '-')
                    .fontSize(14)
                    .fontColor('#333333')
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
                .margin({ bottom: 8 })

                Row() {
                  Text('余额：')
                    .fontSize(14)
                    .fontColor('#666666')
                  Text(MoneyUtil.format(this.customer.balance))
                    .fontSize(14)
                    .fontColor('#007DFF')
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
              }
              .width('100%')
              .padding(16)
              .backgroundColor('#ffffff')
              .margin({ top: 1 })
            }

            // 订单信息
            Column() {
              Row() {
                Text('订单信息')
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
              }
              .width('100%')
              .margin({ bottom: 12 })

              Row() {
                Text('总价：')
                  .fontSize(14)
                  .fontColor('#666666')
                Text(MoneyUtil.format(this.order.totalPrice))
                  .fontSize(16)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#007DFF')
              }
              .width('100%')
              .justifyContent(FlexAlign.SpaceBetween)
              .margin({ bottom: 12 })

              Row() {
                Text('支付方式：')
                  .fontSize(14)
                  .fontColor('#666666')
                Text(this.getPayTypeText(this.order.payType))
                  .fontSize(14)
                  .fontColor('#333333')
              }
              .width('100%')
              .justifyContent(FlexAlign.SpaceBetween)
              .margin({ bottom: 12 })

              Row() {
                Text('加急：')
                  .fontSize(14)
                  .fontColor('#666666')
                Text(this.order.urgent === 1 ? '是' : '否')
                  .fontSize(14)
                  .fontColor('#333333')
              }
              .width('100%')
              .justifyContent(FlexAlign.SpaceBetween)
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#ffffff')
            .margin({ top: 1 })

            // 衣物列表
            Column() {
              Row() {
                Text('衣物明细')
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
              }
              .width('100%')
              .margin({ bottom: 12 })

              if (this.clothesList.length === 0) {
                Text('暂无衣物')
                  .fontSize(14)
                  .fontColor('#999999')
                  .width('100%')
                  .textAlign(TextAlign.Center)
                  .padding(20)
              } else {
                List() {
                  ForEach(this.clothesList, (clothes: Clothes) => {
                    ListItem() {
                      Column() {
                        Row() {
                          Text(clothes.type)
                            .fontSize(14)
                            .fontWeight(FontWeight.Medium)

                          Blank()

                          Text(MoneyUtil.format(clothes.price))
                            .fontSize(14)
                            .fontWeight(FontWeight.Bold)
                            .fontColor('#007DFF')
                        }
                        .width('100%')

                        if (clothes.damageRemark) {
                          Row() {
                            Text('备注：')
                              .fontSize(12)
                              .fontColor('#999999')
                            Text(clothes.damageRemark)
                              .fontSize(12)
                              .fontColor('#666666')
                          }
                          .width('100%')
                          .margin({ top: 4 })
                        }
                      }
                      .width('100%')
                      .padding(12)
                      .backgroundColor('#f9f9f9')
                      .borderRadius(8)
                      .margin({ bottom: 8 })
                    }
                  })
                }
                .width('100%')
              }
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#ffffff')
            .margin({ top: 1 })

            // 状态更新按钮
            if (this.order.status !== 'FINISHED') {
              Column() {
                if (this.order.status === 'UNWASHED') {
                  Button('标记为已洗')
                    .width('90%')
                    .height(48)
                    .fontSize(16)
                    .backgroundColor('#2196F3')
                    .margin({ top: 16 })
                    .onClick(() => {
                      this.updateOrderStatus('WASHED');
                    })
                }

                if (this.order.status === 'WASHED') {
                  Button('标记为已取')
                    .width('90%')
                    .height(48)
                    .fontSize(16)
                    .backgroundColor('#4CAF50')
                    .margin({ top: 16 })
                    .onClick(() => {
                      this.updateOrderStatus('FINISHED');
                    })
                }
              }
              .width('100%')
              .padding(16)
              .backgroundColor('#ffffff')
              .margin({ top: 1 })
            }

            Blank()
              .height(32)
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
}
