import router from '@ohos.router';

/**
 * 欢迎页面 - 入口页面
 */
@Entry
@Component
struct Index {
  build() {
    RelativeContainer() {
      Column() {
        // 标题
        Text('干洗店管理系统')
          .fontSize(32)
          .fontWeight(FontWeight.Bold)
          .margin({ bottom: 16 })

        Text('HarmonyOS 版')
          .fontSize(18)
          .fontColor('#666666')
          .margin({ bottom: 48 })

        // 进入系统按钮
        Button('进入系统')
          .width('80%')
          .height(56)
          .fontSize(20)
          .onClick(() => {
            router.pushUrl({ url: 'pages/Dashboard' });
          })
      }
      .alignRules({
        center: { anchor: '__container__', align: VerticalAlign.Center },
        middle: { anchor: '__container__', align: HorizontalAlign.Center }
      })
    }
    .height('100%')
    .width('100%')
    .backgroundColor('#f5f5f5')
  }
}
