package com.chaser.drycleaningsystem.ui.customer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.ui.components.EmptyStateView
import com.chaser.drycleaningsystem.ui.theme.Primary

/**
 * 客户列表页面 - 现代化设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    viewModel: CustomerViewModel,
    onNavigateBack: () -> Unit,
    onAddCustomer: () -> Unit,
    onEditCustomer: (Customer) -> Unit,
    onCustomerClick: (Customer) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("客户管理")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    Button(
                        onClick = onAddCustomer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.3f),
                            contentColor = Color.White
                        ),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Text("新增客户", style = MaterialTheme.typography.labelLarge, color = Color.White)
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 搜索区域
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "搜索客户姓名或手机号"
            )

            // 客户列表
            when (val state = uiState) {
                is CustomerUiState.Loading -> {
                    LoadingStateView(message = "加载客户中...")
                }
                is CustomerUiState.Success -> {
                    val customers = state.customers.filter {
                        searchQuery.isBlank() ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        (it.phone?.contains(searchQuery) == true)
                    }

                    if (customers.isEmpty()) {
                        EmptyStateView(
                            title = if (searchQuery.isBlank()) "暂无客户" else "未找到客户",
                            message = if (searchQuery.isBlank()) 
                                "点击右上角按钮添加新客户" else 
                                "请尝试其他搜索条件",
                            actionLabel = if (searchQuery.isBlank()) "添加客户" else null,
                            onActionClick = if (searchQuery.isBlank()) {
                                { onAddCustomer() }
                            } else null
                        )
                    } else {
                        // 显示统计信息
                        CustomerStatsBar(totalCustomers = customers.size)

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(customers, key = { it.id }) { customer ->
                                ModernCustomerListItem(
                                    customer = customer,
                                    onEdit = {
                                        Log.d("DRY CLEAN SYSTEM LOG", "点击编辑客户：${customer.name}, ID: ${customer.id}")
                                        onEditCustomer(customer)
                                    },
                                    onDelete = {
                                        Log.d("DRY CLEAN SYSTEM LOG", "点击删除客户：${customer.name}, ID: ${customer.id}")
                                        viewModel.deleteCustomer(customer)
                                    },
                                    onClick = {
                                        Log.d("DRY CLEAN SYSTEM LOG", "点击查看客户详情：${customer.name}, ID: ${customer.id}")
                                        onCustomerClick(customer)
                                    }
                                )
                            }
                        }
                    }
                }
                is CustomerUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("错误：${state.message}")
                    }
                }
            }
        }
    }
}

/**
 * 搜索栏
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = if (value.isNotEmpty()) {
                {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

/**
 * 客户统计栏
 */
@Composable
fun CustomerStatsBar(totalCustomers: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "共 $totalCustomers 位客户",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Primary
                )
            }
        }
    }
}

/**
 * 现代化客户列表项
 */
@Composable
fun ModernCustomerListItem(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 第一行：客户姓名和头像
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 头像
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.2f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = customer.name.firstOrNull()?.toString() ?: "C",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }

                    // 姓名和拼音
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = customer.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // 显示拼音首字母
                            val pinyin = getPinyinInitials(customer.name)
                            if (pinyin.isNotEmpty()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "[$pinyin]",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 操作按钮
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 第二行：联系方式
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!customer.phone.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = customer.phone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (!customer.wechat.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "微信：${customer.wechat}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 第三行：余额
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Primary
                    )
                    Text(
                        text = "余额",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "¥${String.format("%.2f", customer.balance)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // 备注
            if (!customer.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "备注：${customer.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除客户\"${customer.name}\"吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 加载状态视图
 */
@Composable
fun LoadingStateView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 获取中文拼音首字母（简化版）
 */
fun getPinyinInitials(chinese: String): String {
    val pinyinMap = mapOf(
        '张' to 'z', '三' to 's', '李' to 'l', '四' to 's', '王' to 'w', '五' to 'w',
        '赵' to 'z', '钱' to 'q', '孙' to 's', '周' to 'z', '吴' to 'w', '郑' to 'z',
        '冯' to 'f', '陈' to 'c', '褚' to 'c', '卫' to 'w', '蒋' to 'j', '沈' to 's',
        '韩' to 'h', '杨' to 'y', '朱' to 'z', '秦' to 'q', '尤' to 'y', '许' to 'x',
        '何' to 'h', '吕' to 'l', '施' to 's', '张' to 'z', '孔' to 'k', '曹' to 'c',
        '严' to 'y', '华' to 'h', '金' to 'j', '魏' to 'w', '陶' to 't', '姜' to 'j',
        '戚' to 'q', '谢' to 'x', '邹' to 'z', '喻' to 'y', '柏' to 'b', '水' to 's',
        '窦' to 'd', '章' to 'z', '云' to 'y', '苏' to 's', '潘' to 'p', '葛' to 'g',
        '奚' to 'x', '范' to 'f', '彭' to 'p', '郎' to 'l', '鲁' to 'l', '韦' to 'w',
        '昌' to 'c', '马' to 'm', '苗' to 'm', '凤' to 'f', '花' to 'h', '方' to 'f',
        '俞' to 'y', '任' to 'r', '袁' to 'y', '柳' to 'l', '酆' to 'f', '鲍' to 'b',
        '史' to 's', '唐' to 't', '费' to 'f', '廉' to 'l', '岑' to 'c', '薛' to 'x',
        '雷' to 'l', '贺' to 'h', '倪' to 'n', '汤' to 't', '滕' to 't', '殷' to 'y',
        '罗' to 'l', '毕' to 'b', '郝' to 'h', '邬' to 'w', '安' to 'a', '常' to 'c',
        '乐' to 'l', '于' to 'y', '时' to 's', '傅' to 'f', '皮' to 'p', '卞' to 'b',
        '齐' to 'q', '康' to 'k', '伍' to 'w', '余' to 'y', '元' to 'y', '卜' to 'b',
        '顾' to 'g', '孟' to 'm', '平' to 'p', '黄' to 'h', '和' to 'h', '穆' to 'm',
        '萧' to 'x', '尹' to 'y', '姚' to 'y', '邵' to 's', '湛' to 'z', '汪' to 'w',
        '祁' to 'q', '毛' to 'm', '禹' to 'y', '狄' to 'd', '米' to 'm', '贝' to 'b',
        '明' to 'm', '臧' to 'z', '计' to 'j', '伏' to 'f', '成' to 'c', '戴' to 'd',
        '谈' to 't', '宋' to 's', '茅' to 'm', '庞' to 'p', '熊' to 'x', '纪' to 'j',
        '舒' to 's', '屈' to 'q', '项' to 'x', '祝' to 'z', '董' to 'd', '梁' to 'l',
        '杜' to 'd', '阮' to 'r', '蓝' to 'l', '闵' to 'm', '席' to 'x', '季' to 'j',
        '麻' to 'm', '强' to 'q', '贾' to 'j', '路' to 'l', '娄' to 'l', '危' to 'w',
        '江' to 'j', '童' to 't', '颜' to 'y', '郭' to 'g', '梅' to 'm', '盛' to 's',
        '林' to 'l', '刁' to 'd', '钟' to 'z', '徐' to 'x', '邱' to 'q', '骆' to 'l',
        '高' to 'g', '夏' to 'x', '蔡' to 'c', '田' to 't', '樊' to 'f', '胡' to 'h',
        '凌' to 'l', '霍' to 'h', '虞' to 'y', '万' to 'w', '支' to 'z', '柯' to 'k',
        '昝' to 'z', '管' to 'g', '卢' to 'l', '莫' to 'm', '经' to 'j', '房' to 'f',
        '裘' to 'q', '缪' to 'm', '干' to 'g', '解' to 'x', '应' to 'y', '宗' to 'z',
        '丁' to 'd', '宣' to 'x', '邓' to 'd', '郁' to 'y', '单' to 's', '杭' to 'h',
        '洪' to 'h', '包' to 'b', '诸' to 'z', '左' to 'z', '石' to 's', '崔' to 'c',
        '吉' to 'j', '钮' to 'n', '龚' to 'g', '程' to 'c', '嵇' to 'j', '邢' to 'x',
        '滑' to 'h', '裴' to 'p', '陆' to 'l', '荣' to 'r', '翁' to 'w', '荀' to 'x',
        '羊' to 'y', '於' to 'y', '惠' to 'h', '甄' to 'z', '曲' to 'q', '家' to 'j',
        '封' to 'f', '芮' to 'r', '羿' to 'y', '储' to 'c', '靳' to 'j', '汲' to 'j',
        '邴' to 'b', '糜' to 'm', '松' to 's', '井' to 'j', '段' to 'd', '富' to 'f',
        '巫' to 'w', '乌' to 'w', '焦' to 'j', '巴' to 'b', '弓' to 'g', '牧' to 'm',
        '隗' to 'w', '山' to 's', '谷' to 'g', '车' to 'c', '侯' to 'h', '宓' to 'm',
        '蓬' to 'p', '全' to 'q', '郗' to 'x', '班' to 'b', '仰' to 'y', '秋' to 'q',
        '仲' to 'z', '伊' to 'y', '宫' to 'g', '宁' to 'n', '仇' to 'q', '栾' to 'l',
        '暴' to 'b', '甘' to 'g', '钭' to 't', '厉' to 'l', '戎' to 'r', '祖' to 'z',
        '武' to 'w', '符' to 'f', '刘' to 'l', '景' to 'j', '詹' to 'z', '束' to 's',
        '龙' to 'l', '叶' to 'y', '幸' to 'x', '司' to 's', '韶' to 's', '郜' to 'g',
        '黎' to 'l', '蓟' to 'j', '薄' to 'b', '印' to 'y', '宿' to 's', '白' to 'b',
        '怀' to 'h', '蒲' to 'p', '邰' to 't', '从' to 'c', '鄂' to 'e', '索' to 's',
        '咸' to 'x', '籍' to 'j', '赖' to 'l', '卓' to 'z', '蔺' to 'l', '屠' to 't',
        '蒙' to 'm', '池' to 'c', '乔' to 'q', '阴' to 'y', '郁' to 'y', '胥' to 'x',
        '能' to 'n', '苍' to 'c', '双' to 's', '闻' to 'w', '莘' to 's', '党' to 'd',
        '翟' to 'z', '谭' to 't', '贡' to 'g', '劳' to 'l', '逄' to 'p', '姬' to 'j',
        '申' to 's', '扶' to 'f', '堵' to 'd', '冉' to 'r', '宰' to 'z', '郦' to 'l',
        '雍' to 'y', '却' to 'q', '璩' to 'q', '桑' to 's', '桂' to 'g', '濮' to 'p',
        '牛' to 'n', '寿' to 's', '通' to 't', '边' to 'b', '扈' to 'h', '燕' to 'y',
        '冀' to 'j', '郏' to 'j', '浦' to 'p', '尚' to 's', '农' to 'n', '温' to 'w',
        '别' to 'b', '庄' to 'z', '晏' to 'y', '柴' to 'c', '瞿' to 'q', '阎' to 'y',
        '充' to 'c', '慕' to 'm', '连' to 'l', '茹' to 'r', '习' to 'x', '宦' to 'h',
        '艾' to 'a', '鱼' to 'y', '容' to 'r', '向' to 'x', '古' to 'g', '易' to 'y',
        '慎' to 's', '戈' to 'g', '廖' to 'l', '庾' to 'y', '终' to 'z', '暨' to 'j',
        '居' to 'j', '衡' to 'h', '步' to 'b', '都' to 'd', '耿' to 'g', '满' to 'm',
        '弘' to 'h', '匡' to 'k', '国' to 'g', '文' to 'w', '寇' to 'k', '广' to 'g',
        '禄' to 'l', '阙' to 'q', '东' to 'd', '欧' to 'o', '殳' to 's', '沃' to 'w',
        '利' to 'l', '蔚' to 'w', '越' to 'y', '夔' to 'k', '隆' to 'l', '师' to 's',
        '巩' to 'g', '厍' to 's', '聂' to 'n', '晁' to 'c', '勾' to 'g', '敖' to 'a',
        '融' to 'r', '冷' to 'l', '訾' to 'z', '辛' to 'x', '阚' to 'k', '那' to 'n',
        '简' to 'j', '饶' to 'r', '空' to 'k', '曾' to 'z', '母' to 'm', '沙' to 's',
        '乜' to 'n', '养' to 'y', '鞠' to 'j', '须' to 'x', '丰' to 'f', '巢' to 'c',
        '关' to 'g', '蒯' to 'k', '相' to 'x', '查' to 'c', '后' to 'h', '荆' to 'j',
        '红' to 'h', '游' to 'y', '竺' to 'z', '权' to 'q', '逯' to 'l', '盖' to 'g',
        '益' to 'y', '桓' to 'h', '公' to 'g'
    )

    return chinese.mapNotNull { char ->
        pinyinMap[char]
    }.joinToString("").uppercase()
}
