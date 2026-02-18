import router from '@ohos.router';
import { promptAction } from '@kit.ArkUI';
import { Customer } from '../models/Customer';
import { customerService } from '../api/CustomerApi';
import { MoneyUtil } from '../utils/MoneyUtil';
import { globalState } from '../utils/GlobalState';

/**
 * 客户列表页面
 */
@Entry
@Component
struct CustomerList {
  @State customers: Customer[] = [];
  @State isLoading: boolean = true;
  @State searchName: string = '';
  @State searchPhone: string = '';
  private isSelectMode: boolean = false;

  aboutToAppear() {
    // 检查是否为客户选择模式
    const params = router.getParams() as Record<string, string>;
    if (params && params['selectMode'] === 'true') {
      this.isSelectMode = true;
    }
    this.loadCustomers();
  }

  onPageShow() {
    // 页面显示时刷新客户列表（从其他页面返回时）
    this.loadCustomers();
  }

  async loadCustomers() {
    try {
      this.isLoading = true;
      this.customers = await customerService.getAllCustomers();
      console.info(`Loaded ${this.customers.length} customers`);
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to load customers:', error);
      this.isLoading = false;
    }
  }

  async searchCustomers() {
    try {
      this.isLoading = true;
      if (this.searchPhone) {
        // 按手机号搜索
        this.customers = await customerService.fuzzySearch({ phone: this.searchPhone });
      } else if (this.searchName) {
        // 按姓名搜索
        this.customers = await customerService.fuzzySearch({ name: this.searchName });
      } else {
        // 加载所有客户
        this.customers = await customerService.getAllCustomers();
      }
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to search customers:', error);
      this.isLoading = false;
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

        Text('客户管理')
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
        TextInput({ placeholder: '姓名', text: this.searchName })
          .layoutWeight(1)
          .height(40)
          .onChange((value) => {
            this.searchName = value;
          })

        TextInput({ placeholder: '手机号', text: this.searchPhone })
          .layoutWeight(1)
          .height(40)
          .margin({ left: 8, right: 8 })
          .onChange((value) => {
            this.searchPhone = value;
          })

        Button('搜索')
          .width(80)
          .height(40)
          .onClick(() => {
            this.searchCustomers();
          })
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')

      Divider()

      // 客户列表
      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .width(48)
            .height(48)
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else if (this.customers.length === 0) {
        Column() {
          Text('暂无客户')
            .fontSize(16)
            .fontColor('#999999')
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else {
        List() {
          ForEach(this.customers, (customer: Customer) => {
            ListItem() {
              Row() {
                Column() {
                  Text(customer.name)
                    .fontSize(18)
                    .fontWeight(FontWeight.Medium)
                    .margin({ bottom: 4 })

                  Text(customer.phone || '无手机号')
                    .fontSize(14)
                    .fontColor('#666666')
                }
                .alignItems(HorizontalAlign.Start)
                .layoutWeight(1)

                Column() {
                  Text(MoneyUtil.format(customer.balance))
                    .fontSize(16)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#007DFF')
                }
                .alignItems(HorizontalAlign.End)
              }
              .width('100%')
              .padding(16)
              .onClick(() => {
                if (this.isSelectMode) {
                  // 选择模式：保存客户信息并返回
                  globalState.setSelectedCustomer(customer);
                  router.back();
                } else {
                  // 普通模式：跳转到编辑页面
                  router.pushUrl({
                    url: 'pages/CustomerEdit',
                    params: { customerId: customer.id.toString() }
                  });
                }
              })
            }
          })
        }
        .width('100%')
        .layoutWeight(1)
        .divider({ strokeWidth: 1, color: '#f0f0f0', startMargin: 16, endMargin: 16 })
      }

      // 添加客户按钮
      Button('+ 添加客户')
        .width('90%')
        .height(56)
        .fontSize(18)
        .margin({ top: 16, bottom: 16 })
        .onClick(() => {
          router.pushUrl({ url: 'pages/CustomerEdit' });
        })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }
}
