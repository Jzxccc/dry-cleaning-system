import router from '@ohos.router';
import { promptAction } from '@kit.ArkUI';
import { customerService } from '../api/CustomerApi';
import { orderService } from '../api/OrderApi';
import { statisticsService } from '../api/StatisticsApi';
import { DateUtil } from '../utils/DateUtil';
import { MoneyUtil } from '../utils/MoneyUtil';

const LOG_TAG = 'DRY CLEAN SYSTEM LOG:';

/**
 * 功能项类
 */
class FunctionItem {
  name: string;
  url: string;
  color: string;
  
  constructor(name: string, url: string, color: string) {
    this.name = name;
    this.url = url;
    this.color = color;
  }
}

/**
 * 仪表板页面 - 主页面
 */
@Entry
@Component
struct Dashboard {
  @State todayIncome: number = 0;
  @State unfinishedOrders: number = 0;
  @State todayOrders: number = 0;
  @State totalCustomers: number = 0;
  @State isLoading: boolean = true;

  aboutToAppear() {
    console.info(LOG_TAG, 'Dashboard aboutToAppear called');
    this.loadStats();
  }

  async loadStats() {
    try {
      console.info(LOG_TAG, 'loadStats started');
      
      // 加载今日收入
      const today = DateUtil.getToday();
      console.info(LOG_TAG, `Loading daily income for: ${today}`);
      const income = await statisticsService.getDailyIncome(today);
      console.info(LOG_TAG, `Daily income result: ${income}`);
      
      // 加载未完成订单数
      console.info(LOG_TAG, 'Loading unfinished orders count');
      const unfinished = await statisticsService.getUnfinishedOrderCount();
      console.info(LOG_TAG, `Unfinished orders result: ${unfinished}`);
      
      // 加载总客户数
      console.info(LOG_TAG, 'Loading customers');
      const customers = await customerService.getAllCustomers();
      const customerCount = customers.length;
      console.info(LOG_TAG, `Customers result: ${customerCount}`);
      
      // 更新状态（分开赋值确保 UI 更新）
      this.todayIncome = income;
      console.info(LOG_TAG, `Set todayIncome to: ${this.todayIncome}`);
      
      this.unfinishedOrders = unfinished;
      console.info(LOG_TAG, `Set unfinishedOrders to: ${this.unfinishedOrders}`);
      
      this.totalCustomers = customerCount;
      console.info(LOG_TAG, `Set totalCustomers to: ${this.totalCustomers}`);
      
      this.todayOrders = 0;
      this.isLoading = false;
      
      console.info(LOG_TAG, 'loadStats completed');
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load stats:', error);
      this.isLoading = false;
    }
  }

  build() {
    Column() {
      // 顶部导航栏
      Row() {
        Text('干洗店管理系统')
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 16 })
      .backgroundColor('#ffffff')

      // 滚动内容
      Scroll() {
        Column() {
          // 加载状态
          if (this.isLoading) {
            Column() {
              LoadingProgress()
                .width(48)
                .height(48)
              Text('加载中...')
                .fontSize(14)
                .fontColor('#666666')
                .margin({ top: 16 })
            }
            .width('100%')
            .padding(40)
          } else {
            // 快速统计卡片
            Grid() {
              GridItem() {
                this.StatCard('今日收入', MoneyUtil.formatInt(this.todayIncome), '#007DFF')
              }
              GridItem() {
                this.StatCard('未完成订单', this.unfinishedOrders.toString(), '#FF9800')
              }
              GridItem() {
                this.StatCard('今日订单', this.todayOrders.toString(), '#4CAF50')
              }
              GridItem() {
                this.StatCard('总客户数', this.totalCustomers.toString(), '#9C27B0')
              }
            }
            .columnsTemplate('1fr 1fr')
            .width('100%')
            .padding(16)
          }

          Divider()
            .margin({ top: 8, bottom: 16 })

          // 功能入口
          Text('功能菜单')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .width('100%')
            .padding({ left: 20, bottom: 16 })

          // 订单管理
          this.FunctionCard('订单管理', [
            new FunctionItem('新建订单', 'pages/NewOrder', '#4CAF50'),
            new FunctionItem('搜索订单', 'pages/OrderList', '#2196F3')
          ])

          // 客户管理
          this.FunctionCard('客户管理', [
            new FunctionItem('客户列表', 'pages/CustomerList', '#007DFF')
          ])

          // 会员充值
          this.FunctionCard('会员充值', [
            new FunctionItem('会员充值', 'pages/Recharge', '#FF9800')
          ])

          // 统计报表
          this.FunctionCard('统计报表', [
            new FunctionItem('统计报表', 'pages/Statistics', '#9C27B0')
          ])

          // 底部间距
          Blank()
            .height(32)
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

  @Builder
  StatCard(title: string, value: string, color: string) {
    Column() {
      Text(value)
        .fontSize(32)
        .fontWeight(FontWeight.Bold)
        .fontColor(color)
        .margin({ bottom: 8 })

      Text(title)
        .fontSize(14)
        .fontColor('#666666')
    }
    .width('45%')
    .height(120)
    .backgroundColor('#ffffff')
    .borderRadius(12)
    .margin(8)
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }

  @Builder
  FunctionCard(title: string, items: FunctionItem[]) {
    Column() {
      Row() {
        Text(title)
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
      }
      .width('100%')
      .padding({ left: 16, bottom: 12 })

      Row() {
        ForEach(items, (item: FunctionItem) => {
          Button(item.name)
            .flexGrow(1)
            .height(56)
            .fontSize(16)
            .backgroundColor(item.color)
            .margin({ right: 8 })
            .enabled(item.url !== '')
            .onClick(() => {
              if (item.url) {
                router.pushUrl({ url: item.url });
              } else {
                promptAction.showToast({ message: '功能开发中' });
              }
            })
        })
      }
      .width('100%')
      .padding({ left: 16, right: 16, bottom: 16 })
    }
    .width('100%')
    .backgroundColor('#ffffff')
    .margin({ bottom: 16 })
  }
}
