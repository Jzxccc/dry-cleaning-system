import router from '@ohos.router';
import { promptAction } from '@kit.ArkUI';
import { Customer } from '../models/Customer';
import { Order, CreateOrderRequest, Clothes, CreateClothesRequest } from '../models/Order';
import { customerService } from '../api/CustomerApi';
import { orderService } from '../api/OrderApi';
import { MoneyUtil } from '../utils/MoneyUtil';
import { globalState } from '../utils/GlobalState';

/**
 * 衣物类型
 */
class ClothesTypeItem {
  name: string;
  price: number;
  
  constructor(name: string, price: number) {
    this.name = name;
    this.price = price;
  }
}

/**
 * 衣物类型和价格
 */
const CLOTHES_TYPES: ClothesTypeItem[] = [
  new ClothesTypeItem('毛衫', 20),
  new ClothesTypeItem('羊绒大衣 (小)', 20),
  new ClothesTypeItem('羊绒大衣 (中)', 25),
  new ClothesTypeItem('羊绒大衣 (大)', 30),
  new ClothesTypeItem('皮毛一体', 50),
  new ClothesTypeItem('貂', 300),
  new ClothesTypeItem('鞋', 15),
  new ClothesTypeItem('裤子', 20)
];

/**
 * 衣物项目
 */
interface ClothesItem {
  type: string;
  price: number;
  damageRemark: string;
}

/**
 * 新建订单页面
 */
@Entry
@Component
struct NewOrder {
  @State selectedCustomer: Customer | null = null;
  @State clothesItems: ClothesItem[] = [];
  @State totalPrice: number = 0;
  @State payType: string = 'CASH'; // CASH, PREPAID, UNPAID
  @State isUrgent: boolean = false;
  @State isLoading: boolean = false;

  aboutToAppear() {
    // 添加第一件衣物
    this.addClothesItem();
  }

  onPageShow() {
    // 页面显示时检查是否有返回的客户信息
    const customer = globalState.getSelectedCustomer();
    if (customer) {
      this.selectCustomer(customer);
      globalState.clearSelectedCustomer();
    }
  }

  addClothesItem() {
    this.clothesItems.push({
      type: '',
      price: 0,
      damageRemark: ''
    });
  }

  removeClothesItem(index: number) {
    if (this.clothesItems.length > 1) {
      this.clothesItems.splice(index, 1);
      this.calculateTotal();
    } else {
      promptAction.showToast({ message: '至少保留一件衣物' });
    }
  }

  updateClothesType(index: number, type: string) {
    this.clothesItems[index].type = type;
    const clothesType = CLOTHES_TYPES.find((ct: ClothesTypeItem) => ct.name === type);
    if (clothesType) {
      this.clothesItems[index].price = clothesType.price;
    }
    this.calculateTotal();
  }

  updateClothesPrice(index: number, price: number) {
    this.clothesItems[index].price = price;
    this.calculateTotal();
  }

  updateDamageRemark(index: number, remark: string) {
    this.clothesItems[index].damageRemark = remark;
  }

  calculateTotal() {
    this.totalPrice = this.clothesItems.reduce((sum, item) => sum + item.price, 0);
  }

  selectCustomer(customer: Customer) {
    this.selectedCustomer = customer;
  }

  goToCustomerList() {
    router.pushUrl({
      url: 'pages/CustomerList',
      params: { selectMode: 'true' }
    });
  }

  async submitOrder() {
    // 验证客户选择
    if (!this.selectedCustomer) {
      promptAction.showToast({ message: '请选择客户' });
      return;
    }

    // 验证衣物
    const validItems = this.clothesItems.filter(item => item.type && item.price > 0);
    if (validItems.length === 0) {
      promptAction.showToast({ message: '请至少添加一件衣物' });
      return;
    }

    try {
      this.isLoading = true;

      // 生成订单号
      const now = new Date();
      const dateStr = now.getFullYear() +
                     String(now.getMonth() + 1).padStart(2, '0') +
                     String(now.getDate()).padStart(2, '0');
      const randomStr = String(Math.floor(Math.random() * 1000)).padStart(3, '0');
      const orderNo = `ORD-${dateStr}-${randomStr}`;

      // 创建订单
      const orderData: CreateOrderRequest = {
        orderNo: orderNo,
        customerId: this.selectedCustomer.id,
        totalPrice: this.totalPrice,
        prepaid: 0,
        payType: this.payType,
        urgent: this.isUrgent ? 1 : 0,
        status: 'UNWASHED'
      };

      const createdOrder = await orderService.createOrder(orderData);

      // 创建衣物
      for (const item of validItems) {
        const clothesData: CreateClothesRequest = {
          orderId: createdOrder.orderNo,
          type: item.type,
          price: item.price,
          damageRemark: item.damageRemark,
          status: 'UNWASHED'
        };
        await orderService.createClothes(clothesData);
      }

      promptAction.showToast({ message: `订单创建成功！订单号：${orderNo}` });
      this.isLoading = false;
      router.back();
    } catch (error) {
      console.error('Failed to create order:', error);
      this.isLoading = false;
      promptAction.showToast({ message: '订单创建失败，请重试' });
    }
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

        Text('新建订单')
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
                Text(`余额：${MoneyUtil.format(this.selectedCustomer.balance)}`)
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

          // 衣物列表
          Column() {
            Row() {
              Text('衣物明细')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)

              Blank()

              Button('+ 添加衣物')
                .width(100)
                .height(32)
                .fontSize(14)
                .backgroundColor('#f0f0f0')
                .fontColor('#007DFF')
                .onClick(() => {
                  this.addClothesItem();
                })
            }
            .width('100%')
            .margin({ bottom: 12 })

            ForEach(this.clothesItems, (item: ClothesItem, index: number) => {
              Column() {
                Row() {
                  Text(`衣物 ${index + 1}`)
                    .fontSize(14)
                    .fontColor('#666666')

                  Blank()

                  Button('删除')
                    .width(50)
                    .height(28)
                    .fontSize(12)
                    .backgroundColor('#ffebee')
                    .fontColor('#f44336')
                    .enabled(this.clothesItems.length > 1)
                    .onClick(() => {
                      this.removeClothesItem(index);
                    })
                }
                .width('100%')
                .margin({ bottom: 8 })

                // 衣物类型选择
                Column() {
                  ForEach(CLOTHES_TYPES, (clothesType: ClothesTypeItem) => {
                    Row() {
                      Text(clothesType.name)
                        .fontSize(14)
                        .fontColor(item.type === clothesType.name ? '#007DFF' : '#333333')
                        .layoutWeight(1)
                      Text(`${clothesType.price}元`)
                        .fontSize(14)
                        .fontColor('#666666')
                    }
                    .width('100%')
                    .padding(12)
                    .backgroundColor(item.type === clothesType.name ? '#e3f2fd' : '#f5f5f5')
                    .borderRadius(8)
                    .margin({ bottom: 4 })
                    .onClick(() => {
                      this.updateClothesType(index, clothesType.name);
                    })
                  })
                }
                .width('100%')
                .height(200)
                .backgroundColor('#ffffff')
                .borderRadius(8)
                .padding(8)

                // 价格输入
                Row() {
                  Text('价格：')
                    .fontSize(14)
                    .fontColor('#666666')

                  TextInput({ text: item.price.toString() })
                    .width(100)
                    .height(36)
                    .backgroundColor('#f5f5f5')
                    .borderRadius(4)
                    .type(InputType.Number)
                    .onChange((value) => {
                      this.updateClothesPrice(index, parseFloat(value) || 0);
                    })

                  Text('元')
                    .fontSize(14)
                    .fontColor('#666666')
                }
                .width('100%')
                .margin({ top: 8 })

                // 破损备注
                TextInput({ placeholder: '破损备注（选填）', text: item.damageRemark })
                  .width('100%')
                  .height(40)
                  .backgroundColor('#f5f5f5')
                  .borderRadius(4)
                  .padding({ left: 8, right: 8 })
                  .margin({ top: 8 })
                  .onChange((value) => {
                    this.updateDamageRemark(index, value);
                  })
              }
              .width('100%')
              .padding(12)
              .backgroundColor('#f9f9f9')
              .borderRadius(8)
              .margin({ bottom: 8 })
            })
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#ffffff')
          .margin({ bottom: 1 })

          // 订单汇总
          Column() {
            Row() {
              Text('订单汇总')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
            }
            .width('100%')
            .margin({ bottom: 12 })

            // 总价
            Row() {
              Text('总价：')
                .fontSize(16)
                .fontColor('#333333')

              Text(MoneyUtil.format(this.totalPrice))
                .fontSize(24)
                .fontWeight(FontWeight.Bold)
                .fontColor('#007DFF')

              Blank()
            }
            .width('100%')
            .margin({ bottom: 16 })

            // 支付方式
            Row() {
              Text('支付方式：')
                .fontSize(16)
                .fontColor('#333333')

              Row() {
                Button('现金')
                  .height(36)
                  .fontSize(14)
                  .backgroundColor(this.payType === 'CASH' ? '#007DFF' : '#f0f0f0')
                  .fontColor(this.payType === 'CASH' ? '#ffffff' : '#333333')
                  .onClick(() => {
                    this.payType = 'CASH';
                  })

                Button('储值')
                  .height(36)
                  .fontSize(14)
                  .backgroundColor(this.payType === 'PREPAID' ? '#007DFF' : '#f0f0f0')
                  .fontColor(this.payType === 'PREPAID' ? '#ffffff' : '#333333')
                  .margin({ left: 8 })
                  .onClick(() => {
                    this.payType = 'PREPAID';
                  })

                Button('未支付')
                  .height(36)
                  .fontSize(14)
                  .backgroundColor(this.payType === 'UNPAID' ? '#007DFF' : '#f0f0f0')
                  .fontColor(this.payType === 'UNPAID' ? '#ffffff' : '#333333')
                  .margin({ left: 8 })
                  .onClick(() => {
                    this.payType = 'UNPAID';
                  })
              }
            }
            .width('100%')
            .margin({ bottom: 16 })

            // 加急选项
            Row() {
              Text('加急：')
                .fontSize(16)
                .fontColor('#333333')

              Toggle({ type: ToggleType.Switch, isOn: this.isUrgent })
                .onChange((isOn: boolean) => {
                  this.isUrgent = isOn;
                })
            }
            .width('100%')
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#ffffff')
          .margin({ bottom: 1 })

          // 提交按钮
          Button('提交订单')
            .width('90%')
            .height(56)
            .fontSize(18)
            .margin({ top: 24, bottom: 32 })
            .enabled(!this.isLoading && this.totalPrice > 0)
            .onClick(() => {
              this.submitOrder();
            })
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
