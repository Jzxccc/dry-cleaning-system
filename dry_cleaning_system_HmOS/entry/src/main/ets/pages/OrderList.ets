import router from '@ohos.router';
import { Order } from '../models/Order';
import { orderService } from '../api/OrderApi';
import { customerService } from '../api/CustomerApi';
import { Customer } from '../models/Customer';
import { MoneyUtil } from '../utils/MoneyUtil';
import { DateUtil } from '../utils/DateUtil';

const LOG_TAG = 'DRY CLEAN SYSTEM LOG:';

/**
 * 订单列表页面 - 支持多条件查询
 */
@Entry
@Component
struct OrderList {
  @State orders: Order[] = [];
  @State customers: Map<number, Customer> = new Map();
  @State customerPhones: Map<number, string> = new Map();
  @State isLoading: boolean = true;
  
  // 搜索条件
  @State searchOrderNo: string = '';      // 订单号
  @State searchPhone: string = '';         // 手机号
  @State searchCustomerName: string = '';  // 客户姓名
  @State selectedStatus: string = '';      // 状态筛选

  aboutToAppear() {
    this.loadOrders();
    this.loadCustomers();
  }

  async loadOrders() {
    try {
      console.info(LOG_TAG, 'loadOrders started');
      this.isLoading = true;
      
      let filteredOrders = await orderService.getAllOrders();
      console.info(LOG_TAG, `Total orders from DB: ${filteredOrders.length}`);
      
      // 按订单号筛选（前缀匹配）
      if (this.searchOrderNo) {
        filteredOrders = filteredOrders.filter(order => 
          order.orderNo.includes(this.searchOrderNo)
        );
        console.info(LOG_TAG, `After orderNo filter: ${filteredOrders.length}`);
      }
      
      // 按手机号筛选（前缀匹配）
      if (this.searchPhone) {
        const phone = this.searchPhone;
        filteredOrders = filteredOrders.filter(order => {
          const customerPhone = this.customerPhones.get(order.customerId) || '';
          return customerPhone.startsWith(phone);
        });
        console.info(LOG_TAG, `After phone filter: ${filteredOrders.length}`);
      }
      
      // 按客户姓名筛选（支持拼音）
      if (this.searchCustomerName) {
        const keyword = this.searchCustomerName;
        filteredOrders = filteredOrders.filter(order => {
          const customerName = this.getCustomerName(order.customerId);
          return customerName.includes(keyword) || 
                 this.matchesPinyin(customerName, keyword);
        });
        console.info(LOG_TAG, `After name filter: ${filteredOrders.length}`);
      }
      
      // 按状态筛选
      if (this.selectedStatus) {
        filteredOrders = filteredOrders.filter(order => 
          order.status === this.selectedStatus
        );
        console.info(LOG_TAG, `After status filter: ${filteredOrders.length}`);
      }
      
      this.orders = filteredOrders;
      this.isLoading = false;
      console.info(LOG_TAG, `loadOrders completed, result: ${this.orders.length}`);
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load orders:', error);
      this.isLoading = false;
    }
  }

  async loadCustomers() {
    try {
      const customerList = await customerService.getAllCustomers();
      customerList.forEach(c => {
        this.customers.set(c.id, c);
        if (c.phone) {
          this.customerPhones.set(c.id, c.phone);
        }
      });
      console.info(LOG_TAG, `Loaded ${customerList.length} customers`);
    } catch (error) {
      console.error(LOG_TAG, 'Failed to load customers:', error);
    }
  }

  /**
   * 拼音匹配（支持首字母和完整拼音）
   */
  matchesPinyin(chinese: string, pinyin: string): boolean {
    const input = pinyin.toLowerCase();
    
    // 1. 直接包含匹配（支持输入完整拼音或部分拼音）
    if (this.toFullPinyin(chinese).includes(input)) {
      return true;
    }
    
    // 2. 拼音首字母匹配（支持输入首字母缩写）
    const initials = this.toPinyinInitials(chinese);
    if (initials.includes(input)) {
      return true;
    }
    
    return false;
  }

  /**
   * 将中文转换为拼音首字母
   */
  toPinyinInitials(chinese: string): string {
    const pinyinMap: Record<string, string> = {
      '张': 'z', '李': 'l', '王': 'w', '刘': 'l', '陈': 'c',
      '杨': 'y', '赵': 'z', '黄': 'h', '周': 'z', '吴': 'w',
      '徐': 'x', '孙': 's', '马': 'm', '朱': 'z', '胡': 'h',
      '郭': 'g', '何': 'h', '高': 'g', '林': 'l', '罗': 'l',
      '郑': 'z', '梁': 'l', '谢': 'x', '宋': 's', '唐': 't',
      '许': 'x', '邓': 'd', '冯': 'f', '韩': 'h', '曹': 'c',
      '彭': 'p', '曾': 'z', '萧': 'x', '田': 't', '董': 'd',
      '袁': 'y', '潘': 'p', '于': 'y', '蒋': 'j', '蔡': 'c',
      '余': 'y', '杜': 'd', '叶': 'y', '程': 'c', '苏': 's',
      '魏': 'w', '吕': 'l', '丁': 'd', '任': 'r', '沈': 's',
      '姚': 'y', '卢': 'l', '姜': 'j', '崔': 'c', '钟': 'z',
      '谭': 't', '陆': 'l', '汪': 'w', '范': 'f', '金': 'j',
      '石': 's', '廖': 'l', '贾': 'j', '夏': 'x', '韦': 'w',
      '付': 'f', '方': 'f', '白': 'b', '邹': 'z', '孟': 'm',
      '熊': 'x', '秦': 'q', '邱': 'q', '侯': 'h', '邵': 's',
      '史': 's', '毛': 'm', '常': 'c', '万': 'w', '顾': 'g',
      '赖': 'l', '武': 'w', '康': 'k', '贺': 'h', '严': 'y',
      '钱': 'q', '汤': 't', '尹': 'y', '黎': 'l', '易': 'y',
      '牛': 'n', '樊': 'f', '温': 'w', '殷': 'y', '乐': 'l',
      '时': 's', '傅': 'f', '皮': 'p', '齐': 'q', '伍': 'w',
      '元': 'y', '卜': 'b', '平': 'p', '和': 'h', '穆': 'm',
      '祁': 'q', '禹': 'y', '狄': 'd', '米': 'm', '贝': 'b',
      '明': 'm', '臧': 'z', '计': 'j', '伏': 'f', '成': 'c',
      '戴': 'd', '谈': 't', '茅': 'm', '庞': 'p', '纪': 'j',
      '舒': 's', '屈': 'q', '项': 'x', '祝': 'z', '阮': 'r',
      '蓝': 'l', '闵': 'm', '席': 'x', '季': 'j', '麻': 'm',
      '强': 'q', '路': 'l', '娄': 'l', '危': 'w', '江': 'j',
      '童': 't', '颜': 'y', '梅': 'm', '盛': 's', '刁': 'd',
      '骆': 'l', '凌': 'l', '霍': 'h', '虞': 'y', '支': 'z',
      '柯': 'k', '管': 'g', '莫': 'm', '经': 'j', '房': 'f',
      '裘': 'q', '缪': 'm', '干': 'g', '解': 'x', '应': 'y',
      '宗': 'z', '宣': 'x', '郁': 'y', '单': 's', '杭': 'h',
      '洪': 'h', '包': 'b', '诸': 'z', '左': 'z', '吉': 'j',
      '钮': 'n', '龚': 'g', '嵇': 'j', '邢': 'x', '滑': 'h',
      '裴': 'p', '荣': 'r', '翁': 'w', '荀': 'x', '羊': 'y',
      '於': 'y', '惠': 'h', '甄': 'z', '曲': 'q', '家': 'j',
      '封': 'f', '芮': 'r', '羿': 'y', '储': 'c', '靳': 'j',
      '汲': 'j', '邴': 'b', '糜': 'm', '松': 's', '井': 'j',
      '段': 'd', '富': 'f', '巫': 'w', '乌': 'w', '焦': 'j',
      '巴': 'b', '弓': 'g', '牧': 'm', '隗': 'w', '山': 's',
      '谷': 'g', '车': 'c', '宓': 'm', '蓬': 'p', '全': 'q',
      '郗': 'x', '班': 'b', '仰': 'y', '秋': 'q', '仲': 'z',
      '伊': 'y', '宫': 'g', '宁': 'n', '仇': 'c', '栾': 'l',
      '暴': 'b', '甘': 'g', '厉': 'l', '戎': 'r', '祖': 'z',
      '符': 'f', '景': 'j', '詹': 'z', '束': 's', '龙': 'l',
      '幸': 'x', '司': 's', '韶': 's', '郜': 'g', '蓟': 'j',
      '薄': 'b', '印': 'y', '宿': 's', '怀': 'h', '蒲': 'p',
      '邰': 't', '从': 'c', '鄂': 'e', '索': 's', '咸': 'x',
      '籍': 'j', '卓': 'z', '蔺': 'l', '屠': 't', '蒙': 'm',
      '池': 'c', '乔': 'q', '阴': 'y', '胥': 'x', '能': 'n',
      '苍': 'c', '双': 's', '闻': 'w', '莘': 's', '党': 'd',
      '贡': 'g', '劳': 'l', '逄': 'p', '姬': 'j', '申': 's',
      '扶': 'f', '堵': 'd', '冉': 'r', '宰': 'z', '郦': 'l',
      '雍': 'y', '却': 'q', '璩': 'q', '桑': 's', '桂': 'g',
      '濮': 'p', '寿': 's', '通': 't', '边': 'b', '扈': 'h',
      '燕': 'y', '冀': 'j', '郏': 'j', '浦': 'p', '尚': 's',
      '农': 'n', '别': 'b', '庄': 'z', '晏': 'y', '柴': 'c',
      '瞿': 'q', '阎': 'y', '充': 'c', '慕': 'm', '连': 'l',
      '茹': 'r', '习': 'x', '宦': 'h', '艾': 'a', '鱼': 'y',
      '容': 'r', '向': 'x', '古': 'g', '慎': 's', '戈': 'g',
      '庾': 'y', '终': 'z', '暨': 'j', '居': 'j', '衡': 'h',
      '步': 'b', '都': 'd', '耿': 'g', '满': 'm', '弘': 'h',
      '匡': 'k', '国': 'g', '文': 'w', '寇': 'k', '广': 'g',
      '禄': 'l', '阙': 'q', '东': 'd', '欧': 'o', '殳': 's',
      '沃': 'w', '利': 'l', '蔚': 'w', '越': 'y', '隆': 'l',
      '师': 's', '巩': 'g', '厍': 's', '聂': 'n', '晁': 'c',
      '勾': 'g', '敖': 'a', '融': 'r', '冷': 'l', '訾': 'z',
      '辛': 'x', '阚': 'k', '那': 'n', '简': 'j', '饶': 'r',
      '空': 'k', '沙': 's', '养': 'y', '鞠': 'j', '须': 'x',
      '丰': 'f', '巢': 'c', '关': 'g', '蒯': 'k', '相': 'x',
      '查': 'c', '后': 'h', '荆': 'j', '红': 'h', '游': 'y',
      '竺': 'z', '权': 'q', '逯': 'l', '盖': 'g', '益': 'y',
      '桓': 'h', '公': 'g'
    };

    let initials = '';
    for (let char of chinese) {
      if (pinyinMap[char]) {
        initials += pinyinMap[char];
      }
    }
    return initials;
  }

  /**
   * 将中文转换为完整拼音（简化版，仅支持常见姓氏）
   */
  toFullPinyin(chinese: string): string {
    const pinyinFullMap: Record<string, string> = {
      '张': 'zhang', '李': 'li', '王': 'wang', '刘': 'liu', '陈': 'chen',
      '杨': 'yang', '赵': 'zhao', '黄': 'huang', '周': 'zhou', '吴': 'wu',
      '徐': 'xu', '孙': 'sun', '马': 'ma', '朱': 'zhu', '胡': 'hu',
      '郭': 'guo', '何': 'he', '高': 'gao', '林': 'lin', '罗': 'luo',
      '郑': 'zheng', '梁': 'liang', '谢': 'xie', '宋': 'song', '唐': 'tang',
      '许': 'xu', '邓': 'deng', '冯': 'feng', '韩': 'han', '曹': 'cao',
      '彭': 'peng', '曾': 'zeng', '萧': 'xiao', '田': 'tian', '董': 'dong',
      '袁': 'yuan', '潘': 'pan', '于': 'yu', '蒋': 'jiang', '蔡': 'cai',
      '余': 'yu', '杜': 'du', '叶': 'ye', '程': 'cheng', '苏': 'su',
      '魏': 'wei', '吕': 'lv', '丁': 'ding', '任': 'ren', '沈': 'shen',
      '姚': 'yao', '卢': 'lu', '姜': 'jiang', '崔': 'cui', '钟': 'zhong',
      '谭': 'tan', '陆': 'lu', '汪': 'wang', '范': 'fan', '金': 'jin',
      '石': 'shi', '廖': 'liao', '贾': 'jia', '夏': 'xia', '韦': 'wei',
      '付': 'fu', '方': 'fang', '白': 'bai', '邹': 'zou', '孟': 'meng',
      '熊': 'xiong', '秦': 'qin', '邱': 'qiu', '侯': 'hou', '邵': 'shao',
      '史': 'shi', '毛': 'mao', '常': 'chang', '万': 'wan', '顾': 'gu',
      '赖': 'lai', '武': 'wu', '康': 'kang', '贺': 'he', '严': 'yan',
      '钱': 'qian', '汤': 'tang', '尹': 'yin', '黎': 'li', '易': 'yi',
      '牛': 'niu', '樊': 'fan', '温': 'wen', '殷': 'yin', '乐': 'le',
      '时': 'shi', '傅': 'fu', '皮': 'pi', '齐': 'qi', '伍': 'wu',
      '元': 'yuan', '卜': 'bu', '平': 'ping', '和': 'he', '穆': 'mu',
      '祁': 'qi', '禹': 'yu', '狄': 'di', '米': 'mi', '贝': 'bei',
      '明': 'ming', '臧': 'zang', '计': 'ji', '伏': 'fu', '成': 'cheng',
      '戴': 'dai', '谈': 'tan', '茅': 'mao', '庞': 'pang', '纪': 'ji',
      '舒': 'shu', '屈': 'qu', '项': 'xiang', '祝': 'zhu', '阮': 'ruan',
      '蓝': 'lan', '闵': 'min', '席': 'xi', '季': 'ji', '麻': 'ma',
      '强': 'qiang', '路': 'lu', '娄': 'lou', '危': 'wei', '江': 'jiang',
      '童': 'tong', '颜': 'yan', '梅': 'mei', '盛': 'sheng', '刁': 'diao',
      '骆': 'luo', '凌': 'ling', '霍': 'huo', '虞': 'yu', '支': 'zhi',
      '柯': 'ke', '管': 'guan', '莫': 'mo', '经': 'jing', '房': 'fang',
      '裘': 'qiu', '缪': 'miao', '干': 'gan', '解': 'xie', '应': 'ying',
      '宗': 'zong', '宣': 'xuan', '郁': 'yu', '单': 'shan', '杭': 'hang',
      '洪': 'hong', '包': 'bao', '诸': 'zhu', '左': 'zuo', '吉': 'ji',
      '钮': 'niu', '龚': 'gong', '嵇': 'ji', '邢': 'xing', '滑': 'hua',
      '裴': 'pei', '荣': 'rong', '翁': 'weng', '荀': 'xun', '羊': 'yang',
      '於': 'yu', '惠': 'hui', '甄': 'zhen', '曲': 'qu', '家': 'jia',
      '封': 'feng', '芮': 'rui', '羿': 'yi', '储': 'chu', '靳': 'jin',
      '汲': 'ji', '邴': 'bing', '糜': 'mi', '松': 'song', '井': 'jing',
      '段': 'duan', '富': 'fu', '巫': 'wu', '乌': 'wu', '焦': 'jiao',
      '巴': 'ba', '弓': 'gong', '牧': 'mu', '隗': 'wei', '山': 'shan',
      '谷': 'gu', '车': 'che', '宓': 'mi', '蓬': 'peng', '全': 'quan',
      '郗': 'xi', '班': 'ban', '仰': 'yang', '秋': 'qiu', '仲': 'zhong',
      '伊': 'yi', '宫': 'gong', '宁': 'ning', '仇': 'chou', '栾': 'luan',
      '暴': 'bao', '甘': 'gan', '厉': 'li', '戎': 'rong', '祖': 'zu',
      '符': 'fu', '景': 'jing', '詹': 'zhan', '束': 'shu', '龙': 'long',
      '幸': 'xing', '司': 'si', '韶': 'shao', '郜': 'gao', '蓟': 'ji',
      '薄': 'bo', '印': 'yin', '宿': 'su', '怀': 'huai', '蒲': 'pu',
      '邰': 'tai', '从': 'cong', '鄂': 'e', '索': 'suo', '咸': 'xian',
      '籍': 'ji', '卓': 'zhuo', '蔺': 'lin', '屠': 'tu', '蒙': 'meng',
      '池': 'chi', '乔': 'qiao', '阴': 'yin', '胥': 'xu', '能': 'neng',
      '苍': 'cang', '双': 'shuang', '闻': 'wen', '莘': 'shen', '党': 'dang',
      '贡': 'gong', '劳': 'lao', '逄': 'pang', '姬': 'ji', '申': 'shen',
      '扶': 'fu', '堵': 'du', '冉': 'ran', '宰': 'zai', '郦': 'li',
      '雍': 'yong', '却': 'que', '璩': 'qu', '桑': 'sang', '桂': 'gui',
      '濮': 'pu', '寿': 'shou', '通': 'tong', '边': 'bian', '扈': 'hu',
      '燕': 'yan', '冀': 'ji', '郏': 'jia', '浦': 'pu', '尚': 'shang',
      '农': 'nong', '别': 'bie', '庄': 'zhuang', '晏': 'yan', '柴': 'chai',
      '瞿': 'qu', '阎': 'yan', '充': 'chong', '慕': 'mu', '连': 'lian',
      '茹': 'ru', '习': 'xi', '宦': 'huan', '艾': 'ai', '鱼': 'yu',
      '容': 'rong', '向': 'xiang', '古': 'gu', '慎': 'shen', '戈': 'ge',
      '庾': 'yu', '终': 'zhong', '暨': 'ji', '居': 'ju', '衡': 'heng',
      '步': 'bu', '都': 'du', '耿': 'geng', '满': 'man', '弘': 'hong',
      '匡': 'kuang', '国': 'guo', '文': 'wen', '寇': 'kou', '广': 'guang',
      '禄': 'lu', '阙': 'que', '东': 'dong', '欧': 'ou', '殳': 'shu',
      '沃': 'wo', '利': 'li', '蔚': 'wei', '越': 'yue', '隆': 'long',
      '师': 'shi', '巩': 'gong', '厍': 'she', '聂': 'nie', '晁': 'chao',
      '勾': 'gou', '敖': 'ao', '融': 'rong', '冷': 'leng', '訾': 'zi',
      '辛': 'xin', '阚': 'kan', '那': 'na', '简': 'jian', '饶': 'rao',
      '空': 'kong', '沙': 'sha', '养': 'yang', '鞠': 'ju', '须': 'xu',
      '丰': 'feng', '巢': 'chao', '关': 'guan', '蒯': 'kuai', '相': 'xiang',
      '查': 'zha', '后': 'hou', '荆': 'jing', '红': 'hong', '游': 'you',
      '竺': 'zhu', '权': 'quan', '逯': 'lu', '盖': 'gai', '益': 'yi',
      '桓': 'huan', '公': 'gong'
    };

    let pinyin = '';
    for (let char of chinese) {
      if (pinyinFullMap[char]) {
        pinyin += pinyinFullMap[char];
      }
    }
    return pinyin;
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

  onSearch() {
    console.info(LOG_TAG, `Search - orderNo: "${this.searchOrderNo}", phone: "${this.searchPhone}", name: "${this.searchCustomerName}", status: "${this.selectedStatus}"`);
    this.loadOrders();
  }

  onReset() {
    this.searchOrderNo = '';
    this.searchPhone = '';
    this.searchCustomerName = '';
    this.selectedStatus = '';
    this.loadOrders();
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

      // 搜索区域
      Column() {
        // 第一行：订单号 + 手机号
        Row() {
          TextInput({ 
            placeholder: '订单号', 
            text: this.searchOrderNo 
          })
            .layoutWeight(1)
            .height(40)
            .backgroundColor('#f5f5f5')
            .borderRadius(8)
            .padding({ left: 12, right: 12 })
            .onChange((value) => {
              this.searchOrderNo = value;
            })

          TextInput({ 
            placeholder: '手机号', 
            text: this.searchPhone 
          })
            .layoutWeight(1)
            .height(40)
            .backgroundColor('#f5f5f5')
            .borderRadius(8)
            .padding({ left: 12, right: 12 })
            .margin({ left: 8, right: 8 })
            .type(InputType.Number)
            .onChange((value) => {
              this.searchPhone = value;
            })

          Button('搜索')
            .width(80)
            .height(40)
            .onClick(() => {
              this.onSearch();
            })
        }
        .width('100%')

        // 第二行：客户姓名 + 重置按钮
        Row() {
          TextInput({ 
            placeholder: '客户姓名（支持拼音）', 
            text: this.searchCustomerName 
          })
            .layoutWeight(1)
            .height(40)
            .backgroundColor('#f5f5f5')
            .borderRadius(8)
            .padding({ left: 12, right: 12 })
            .onChange((value) => {
              this.searchCustomerName = value;
            })

          Button('重置')
            .width(80)
            .height(40)
            .margin({ left: 8 })
            .backgroundColor('#f0f0f0')
            .fontColor('#333333')
            .onClick(() => {
              this.onReset();
            })
        }
        .width('100%')
        .margin({ top: 8 })
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')
      .margin({ bottom: 1 })

      // 状态筛选
      Row() {
        Button('全部')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === '' ? '#007DFF' : '#f0f0f0')
          .fontColor(this.selectedStatus === '' ? '#ffffff' : '#333333')
          .onClick(() => {
            this.selectedStatus = '';
            this.onSearch();
          })

        Button('未洗')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === 'UNWASHED' ? '#FF9800' : '#f0f0f0')
          .fontColor(this.selectedStatus === 'UNWASHED' ? '#ffffff' : '#333333')
          .margin({ left: 4, right: 4 })
          .onClick(() => {
            this.selectedStatus = 'UNWASHED';
            this.onSearch();
          })

        Button('已洗')
          .flexGrow(1)
          .height(40)
          .fontSize(14)
          .backgroundColor(this.selectedStatus === 'WASHED' ? '#2196F3' : '#f0f0f0')
          .fontColor(this.selectedStatus === 'WASHED' ? '#ffffff' : '#333333')
          .margin({ left: 4, right: 4 })
          .onClick(() => {
            this.selectedStatus = 'WASHED';
            this.onSearch();
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
            this.onSearch();
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
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }
}
