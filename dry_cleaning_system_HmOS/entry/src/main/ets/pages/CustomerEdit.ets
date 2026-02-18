import router from '@ohos.router';
import { promptAction } from '@kit.ArkUI';
import { Customer, CreateCustomerRequest } from '../models/Customer';
import { customerService } from '../api/CustomerApi';

/**
 * 客户编辑页面 - 用于添加和编辑客户
 */
@Entry
@Component
struct CustomerEdit {
  @State name: string = '';
  @State phone: string = '';
  @State wechat: string = '';
  @State isEditing: boolean = false;
  @State customerId: number = 0;
  @State isLoading: boolean = false;

  aboutToAppear() {
    // 检查是否有编辑参数
    const params = router.getParams() as Record<string, string>;
    if (params && params['customerId']) {
      this.isEditing = true;
      this.customerId = parseInt(params['customerId']);
      this.loadCustomer();
    }
  }

  async loadCustomer() {
    try {
      this.isLoading = true;
      const customer = await customerService.getCustomerById(this.customerId);
      if (customer) {
        this.name = customer.name;
        this.phone = customer.phone || '';
        this.wechat = customer.wechat || '';
      }
      this.isLoading = false;
    } catch (error) {
      console.error('Failed to load customer:', error);
      this.isLoading = false;
      promptAction.showToast({ message: '加载客户信息失败' });
    }
  }

  async saveCustomer() {
    // 验证必填字段
    if (!this.name.trim() && !this.phone.trim()) {
      promptAction.showToast({ message: '姓名和手机号至少填写一项' });
      return;
    }

    try {
      this.isLoading = true;

      if (this.isEditing) {
        // 编辑客户
        await customerService.updateCustomer(this.customerId, {
          name: this.name,
          phone: this.phone,
          wechat: this.wechat
        });
        promptAction.showToast({ message: '客户信息更新成功' });
      } else {
        // 添加客户
        const newCustomer: CreateCustomerRequest = {
          name: this.name.trim(),
          phone: this.phone.trim(),
          wechat: this.wechat.trim() || undefined,
          balance: 0
        };

        const savedCustomer = await customerService.createCustomer(newCustomer);
        console.info(`Customer created with ID: ${savedCustomer.id}`);
        promptAction.showToast({ message: '客户添加成功' });
      }

      this.isLoading = false;
      router.back();
    } catch (error) {
      console.error('Failed to save customer:', error);
      this.isLoading = false;
      promptAction.showToast({ message: '保存失败，请重试' });
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

        Text(this.isEditing ? '编辑客户' : '添加客户')
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
      } else {
        // 表单内容
        Scroll() {
          Column() {
            // 姓名输入框
            Column() {
              Text('姓名')
                .fontSize(16)
                .fontColor('#333333')
                .margin({ bottom: 8 })

              TextInput({ placeholder: '请输入客户姓名', text: this.name })
                .height(50)
                .backgroundColor('#f5f5f5')
                .borderRadius(8)
                .padding({ left: 16, right: 16 })
                .onChange((value) => {
                  this.name = value;
                })
            }
            .width('100%')
            .padding(20)
            .backgroundColor('#ffffff')
            .margin({ bottom: 1 })

            // 手机号输入框
            Column() {
              Text('手机号')
                .fontSize(16)
                .fontColor('#333333')
                .margin({ bottom: 8 })

              TextInput({ placeholder: '请输入手机号', text: this.phone })
                .height(50)
                .backgroundColor('#f5f5f5')
                .borderRadius(8)
                .padding({ left: 16, right: 16 })
                .type(InputType.Number)
                .onChange((value) => {
                  this.phone = value;
                })
            }
            .width('100%')
            .padding(20)
            .backgroundColor('#ffffff')
            .margin({ bottom: 1 })

            // 微信输入框
            Column() {
              Text('微信 (选填)')
                .fontSize(16)
                .fontColor('#333333')
                .margin({ bottom: 8 })

              TextInput({ placeholder: '请输入微信号', text: this.wechat })
                .height(50)
                .backgroundColor('#f5f5f5')
                .borderRadius(8)
                .padding({ left: 16, right: 16 })
                .onChange((value) => {
                  this.wechat = value;
                })
            }
            .width('100%')
            .padding(20)
            .backgroundColor('#ffffff')
            .margin({ bottom: 1 })

            // 提示信息
            Row() {
              Text('* 姓名和手机号至少填写一项')
                .fontSize(14)
                .fontColor('#999999')
            }
            .width('100%')
            .padding(20)
            .backgroundColor('#ffffff')
            .justifyContent(FlexAlign.Start)

            // 保存按钮
            Button(this.isEditing ? '保存修改' : '添加客户')
              .width('90%')
              .height(56)
              .fontSize(18)
              .margin({ top: 32 })
              .enabled(!this.isLoading)
              .onClick(() => {
                this.saveCustomer();
              })

            // 删除按钮（仅编辑模式）
            if (this.isEditing) {
              Button('删除客户')
                .width('90%')
                .height(56)
                .fontSize(18)
                .margin({ top: 16 })
                .backgroundColor('#ff4444')
                .enabled(!this.isLoading)
                .onClick(() => {
                  this.deleteCustomer();
                })
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

  // 删除客户
  async deleteCustomer() {
    promptAction.showDialog({
      title: '确认删除',
      message: '确定要删除该客户吗？删除后无法恢复。',
      buttons: [
        { text: '取消', color: '#666666' },
        { text: '删除', color: '#ff4444' }
      ]
    }).then(async (result) => {
      if (result.index === 1) {
        try {
          this.isLoading = true;
          await customerService.deleteCustomer(this.customerId);
          promptAction.showToast({ message: '客户已删除' });
          this.isLoading = false;
          router.back();
        } catch (error) {
          console.error('Failed to delete customer:', error);
          this.isLoading = false;
          promptAction.showToast({ message: '删除失败，请重试' });
        }
      }
    });
  }
}
