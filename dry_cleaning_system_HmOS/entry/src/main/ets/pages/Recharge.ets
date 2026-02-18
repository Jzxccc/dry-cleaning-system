import router from '@ohos.router';
import { promptAction } from '@kit.ArkUI';
import { Customer } from '../models/Customer';
import { RechargeRecord } from '../models/RechargeRecord';
import { customerService } from '../api/CustomerApi';
import { prepaidService } from '../api/PrepaidApi';
import { MoneyUtil } from '../utils/MoneyUtil';
import { DateUtil } from '../utils/DateUtil';
import { globalState } from '../utils/GlobalState';

/**
 * 会员充值页面
 */
@Entry
@Component
struct Recharge {
  @State selectedCustomer: Customer | null = null;
  @State rechargeAmount: string = '';
  @State giftAmount: number = 0;
  @State totalAmount: number = 0;
  @State rechargeRecords: RechargeRecord[] = [];
  @State isLoading: boolean = false;

  aboutToAppear() {
    this.loadRechargeRecords();
  }

  onPageShow() {
    // 页面显示时检查是否有返回的客户信息
    const customer = globalState.getSelectedCustomer();
    if (customer) {
      this.selectedCustomer = customer;
      globalState.clearSelectedCustomer();
    }
  }

  // 更新充值金额时计算赠送
  updateRechargeAmount(value: string) {
    this.rechargeAmount = value;
    const amount = parseFloat(value) || 0;
    
    // 验证是否为 100 的整数倍
    if (amount > 0 && amount % 100 !== 0) {
      this.giftAmount = 0;
      this.totalAmount = 0;
    } else {
      this.giftAmount = MoneyUtil.calculateGift(amount);
      this.totalAmount = MoneyUtil.calculateTotal(amount);
    }
  }

  async loadRechargeRecords() {
    try {
      // 加载最近充值记录（简化处理，加载所有客户的记录）
      const customers = await customerService.getAllCustomers();
      const allRecords: RechargeRecord[] = [];
      
      for (const customer of customers.slice(0, 10)) {
        const records = await prepaidService.getRechargeRecords(customer.id);
        records.forEach(r => {
          const record: RechargeRecord = {
            id: r.id,
            customerId: customer.id,
            rechargeAmount: r.rechargeAmount,
            giftAmount: r.giftAmount,
            createTime: r.createTime
          };
          allRecords.push(record);
        });
      }
      
      // 按时间排序，取最近 10 条
      this.rechargeRecords = allRecords
        .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
        .slice(0, 10);
    } catch (error) {
      console.error('Failed to load recharge records:', error);
    }
  }

  async submitRecharge() {
    // 验证客户选择
    if (!this.selectedCustomer) {
      promptAction.showToast({ message: '请选择客户' });
      return;
    }

    // 验证充值金额
    const amount = parseFloat(this.rechargeAmount);
    if (!amount || amount <= 0) {
      promptAction.showToast({ message: '请输入充值金额' });
      return;
    }

    if (amount % 100 !== 0) {
      promptAction.showToast({ message: '充值金额必须是 100 的整数倍' });
      return;
    }

    try {
      this.isLoading = true;

      const result = await prepaidService.recharge({
        customerId: this.selectedCustomer.id,
        amount: amount
      });

      promptAction.showToast({ message: `充值成功！${result}` });
      this.isLoading = false;

      // 更新客户余额显示
      if (this.selectedCustomer) {
        const giftAmount = amount * 0.2;
        this.selectedCustomer.balance = (this.selectedCustomer.balance || 0) + amount + giftAmount;
      }

      // 重置表单
      this.rechargeAmount = '';
      this.giftAmount = 0;
      this.totalAmount = 0;

      // 刷新记录
      this.loadRechargeRecords();
    } catch (error) {
      console.error('Failed to recharge:', error);
      this.isLoading = false;
      promptAction.showToast({ message: '充值失败，请重试' });
    }
  }

  goToCustomerList() {
    router.pushUrl({
      url: 'pages/CustomerList',
      params: { selectMode: 'true' }
    });
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

        Text('会员充值')
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

      Scroll() {
        Column() {
          // 客户选择
          Column() {
            Row() {
              Text('客户信息')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)

              Blank()

              if (this.selectedCustomer) {
                Button('更换')
                  .width(60)
                  .height(32)
                  .fontSize(14)
                  .backgroundColor('#f0f0f0')
                  .fontColor('#333333')
                  .onClick(() => {
                    this.goToCustomerList();
                  })
              }
            }
            .width('100%')
            .margin({ bottom: 12 })

            if (this.selectedCustomer) {
              Column() {
                Text(`${this.selectedCustomer.name}  ${this.selectedCustomer.phone}`)
                  .fontSize(16)
                  .fontColor('#333333')
                Text(`当前余额：${MoneyUtil.format(this.selectedCustomer.balance)}`)
                  .fontSize(14)
                  .fontColor('#007DFF')
                  .margin({ top: 4 })
              }
              .alignItems(HorizontalAlign.Start)
            } else {
              Button('选择客户')
                .width('100%')
                .height(48)
                .backgroundColor('#007DFF')
                .onClick(() => {
                  this.goToCustomerList();
                })
            }
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#ffffff')
          .margin({ bottom: 1 })

          // 充值金额输入
          Column() {
            Row() {
              Text('充值金额')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
            }
            .width('100%')
            .margin({ bottom: 12 })

            TextInput({ placeholder: '请输入充值金额（100 的整数倍）', text: this.rechargeAmount })
              .width('100%')
              .height(56)
              .fontSize(20)
              .backgroundColor('#f5f5f5')
              .borderRadius(8)
              .padding({ left: 16, right: 16 })
              .type(InputType.Number)
              .onChange((value) => {
                this.updateRechargeAmount(value);
              })

            // 快捷金额按钮
            Row() {
              ForEach([100, 200, 300, 500], (amount: number) => {
                Button(`¥${amount}`)
                  .flexGrow(1)
                  .height(40)
                  .fontSize(14)
                  .backgroundColor('#f0f0f0')
                  .fontColor('#333333')
                  .margin({ right: 4 })
                  .onClick(() => {
                    this.updateRechargeAmount(amount.toString());
                  })
              })
            }
            .width('100%')
            .margin({ top: 12 })

            // 充值说明
            Row() {
              Text('充值优惠：充 100 送 20%')
                .fontSize(14)
                .fontColor('#666666')
            }
            .width('100%')
            .margin({ top: 8 })
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#ffffff')
          .margin({ bottom: 1 })

          // 充值详情
          if (this.rechargeAmount && parseFloat(this.rechargeAmount) > 0) {
            Column() {
              Row() {
                Text('充值详情')
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
              }
              .width('100%')
              .margin({ bottom: 16 })

              Row() {
                Text('充值金额：')
                  .fontSize(16)
                  .fontColor('#333333')

                Text(MoneyUtil.format(parseFloat(this.rechargeAmount)))
                  .fontSize(20)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#007DFF')
              }
              .width('100%')
              .justifyContent(FlexAlign.SpaceBetween)
              .margin({ bottom: 12 })

              Row() {
                Text('赠送金额：')
                  .fontSize(16)
                  .fontColor('#333333')

                Text(`+${MoneyUtil.format(this.giftAmount)}`)
                  .fontSize(20)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#4CAF50')
              }
              .width('100%')
              .justifyContent(FlexAlign.SpaceBetween)
              .margin({ bottom: 12 })

              Divider()
                .margin({ top: 12, bottom: 12 })

              Row() {
                Text('实际到账：')
                  .fontSize(18)
                  .fontWeight(FontWeight.Medium)

                Text(MoneyUtil.format(this.totalAmount))
                  .fontSize(24)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#007DFF')
              }
              .width('100%')
              .justifyContent(FlexAlign.SpaceBetween)
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#ffffff')
            .margin({ bottom: 1 })
          }

          // 充值说明
          Column() {
            Row() {
              Text('充值说明')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
            }
            .width('100%')
            .margin({ bottom: 12 })

            Column() {
              Text('• 充值金额必须是 100 的整数倍')
                .fontSize(14)
                .fontColor('#666666')
                .margin({ bottom: 4 })
              Text('• 充值 100 元赠送 20 元，充 200 元赠送 40 元，以此类推')
                .fontSize(14)
                .fontColor('#666666')
                .margin({ bottom: 4 })
              Text('• 充值后金额立即到账，可用于消费')
                .fontSize(14)
                .fontColor('#666666')
            }
            .width('100%')
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#ffffff')
          .margin({ bottom: 1 })

          // 提交按钮
          Button('确认充值')
            .width('90%')
            .height(56)
            .fontSize(18)
            .margin({ top: 24, bottom: 16 })
            .enabled(!this.isLoading && !!this.selectedCustomer && parseFloat(this.rechargeAmount) > 0)
            .onClick(() => {
              this.submitRecharge();
            })

          // 最近充值记录
          Column() {
            Row() {
              Text('最近充值记录')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
            }
            .width('100%')
            .margin({ bottom: 12 })

            if (this.rechargeRecords.length === 0) {
              Text('暂无充值记录')
                .fontSize(14)
                .fontColor('#999999')
                .width('100%')
                .textAlign(TextAlign.Center)
                .padding(20)
            } else {
              List() {
                ForEach(this.rechargeRecords, (record: RechargeRecord) => {
                  ListItem() {
                    Column() {
                      Row() {
                        Text(`客户 ID: ${record.customerId}`)
                          .fontSize(14)
                          .fontColor('#666666')

                        Blank()

                        Text(DateUtil.formatDate(DateUtil.parseDate(record.createTime)))
                          .fontSize(12)
                          .fontColor('#999999')
                      }
                      .width('100%')

                      Row() {
                        Text(`充值：${MoneyUtil.format(record.rechargeAmount)}`)
                          .fontSize(14)
                          .fontColor('#333333')

                        Text(`+ 赠送：${MoneyUtil.format(record.giftAmount)}`)
                          .fontSize(14)
                          .fontColor('#4CAF50')
                          .margin({ left: 16 })
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
          .padding(16)
          .backgroundColor('#ffffff')
          .margin({ bottom: 32 })
        }
        .width('100%')
      }
      .layoutWeight(1)
      .scrollBar(BarState.Off)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }
}
