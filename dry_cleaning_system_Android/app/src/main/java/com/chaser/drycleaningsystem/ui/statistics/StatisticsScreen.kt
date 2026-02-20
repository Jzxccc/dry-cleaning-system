package com.chaser.drycleaningsystem.ui.statistics

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.ui.order.OrderPayTypeText
import com.chaser.drycleaningsystem.ui.order.StatusBadge
import java.text.SimpleDateFormat
import java.util.*

/**
 * 统计报表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val reportType by viewModel.reportType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计报表") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tab 切换
            TabRow(
                selectedTabIndex = if (reportType == ReportType.DAILY) 0 else 1
            ) {
                Tab(
                    selected = reportType == ReportType.DAILY,
                    onClick = { viewModel.setReportType(ReportType.DAILY) }
                ) {
                    Text("日报")
                }
                Tab(
                    selected = reportType == ReportType.MONTHLY,
                    onClick = { viewModel.setReportType(ReportType.MONTHLY) }
                ) {
                    Text("月报")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 日期选择器
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (reportType) {
                                ReportType.DAILY -> "选择日期"
                                ReportType.MONTHLY -> "选择月份"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        OutlinedButton(
                            onClick = { showDatePicker = true }
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("选择")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 统计卡片
                when (val state = uiState) {
                    is StatisticsUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is StatisticsUiState.Success -> {
                        // 日期标题
                        item {
                            Text(
                                text = state.dateStr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // 收入统计卡片
                        item {
                            IncomeStatCard(
                                totalIncome = state.totalIncome,
                                cashIncome = state.cashIncome,
                                rechargeIncome = state.rechargeIncome,
                                pendingOrders = state.pendingOrders
                            )
                        }

                        // 订单明细标题
                        item {
                            Text(
                                text = "订单明细",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                            )
                        }

                        // 订单明细列表
                        if (state.orders.isEmpty()) {
                            item {
                                Text(
                                    text = "暂无订单",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(state.orders, key = { it.id }) { order ->
                                OrderSummaryItem(order = order)
                            }
                        }
                    }
                    is StatisticsUiState.Error -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("错误：${state.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    // 日期选择器对话框
    if (showDatePicker) {
        DatePickerDialog(
            reportType = reportType,
            selectedDate = selectedDate,
            onDateSelected = { timestamp ->
                viewModel.setSelectedDate(timestamp)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

/**
 * 收入统计卡片
 */
@Composable
fun IncomeStatCard(
    totalIncome: Double,
    cashIncome: Double,
    rechargeIncome: Double,
    pendingOrders: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 总收入
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "总收入",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "¥${String.format("%.2f", totalIncome)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // 现金收入
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "现金收入",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "¥${String.format("%.2f", cashIncome)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 充值收入
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "充值收入",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "¥${String.format("%.2f", rechargeIncome)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 未完成订单数
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "待取件订单",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$pendingOrders 单",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (pendingOrders > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 订单汇总列表项
 */
@Composable
fun OrderSummaryItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "订单号：${order.orderNo}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "¥${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "支付方式：${OrderPayTypeText(order.payType)}",
                    style = MaterialTheme.typography.bodySmall
                )
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "时间：${formatDateTime(order.createTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 日期选择器对话框
 */
@Composable
fun DatePickerDialog(
    reportType: ReportType,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    calendar.time = Date(selectedDate)
    
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val startYear = 2020 // 起始年份
    val years = (startYear..currentYear).toList()
    
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    
    val context = LocalContext.current
    var showAndroidDatePicker by remember { mutableStateOf(false) }
    
    // 计算已选择年份的索引，用于初始滚动位置
    val selectedYearIndex = remember { years.indexOf(selectedYear).coerceAtLeast(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedYearIndex.coerceAtLeast(1) - 1)

    if (reportType == ReportType.DAILY && showAndroidDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                onDateSelected(cal.timeInMillis)
            },
            selectedYear,
            selectedMonth,
            selectedDay
        ).apply {
            setOnDismissListener { onDismiss() }
            show()
        }
        // 立即调用 onDismiss，因为 Android 原生对话框会处理取消
        onDismiss()
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (reportType) {
                    ReportType.DAILY -> "选择日期"
                    ReportType.MONTHLY -> "选择月份"
                }
            )
        },
        text = {
            if (reportType == ReportType.DAILY) {
                // 日报 - 触发原生 DatePicker
                LaunchedEffect(Unit) {
                    showAndroidDatePicker = true
                }
            } else {
                // 月报 - 年月选择器
                Column {
                    Text("选择年份：")
                    Spacer(modifier = Modifier.height(8.dp))
                    // 年份选择 - 滚轮样式，可滚动到当前年份
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .width(120.dp)
                            .height(150.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        state = listState
                    ) {
                        items(years) { year ->
                            val isSelected = year == selectedYear
                            Text(
                                text = "$year 年",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 8.dp)
                                    .clickable { selectedYear = year }
                                    .then(
                                        if (isSelected) {
                                            Modifier.padding(4.dp)
                                        } else {
                                            Modifier
                                        }
                                    ),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = if (isSelected) {
                                    MaterialTheme.typography.titleMedium
                                } else {
                                    MaterialTheme.typography.bodyLarge
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("提示：上下滑动选择年份")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("选择月份：")
                    Spacer(modifier = Modifier.height(8.dp))
                    // 月份选择 - 两行显示
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 第一行：1-6 月
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (1..6).forEach { month ->
                                FilterChip(
                                    selected = month - 1 == selectedMonth,
                                    onClick = { selectedMonth = month - 1 },
                                    label = { Text("$month 月") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        // 第二行：7-12 月
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (7..12).forEach { month ->
                                FilterChip(
                                    selected = month - 1 == selectedMonth,
                                    onClick = { selectedMonth = month - 1 },
                                    label = { Text("$month 月") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (reportType == ReportType.MONTHLY) {
                TextButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(selectedYear, selectedMonth, 1)
                        onDateSelected(cal.timeInMillis)
                    }
                ) {
                    Text("确定")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 格式化时间
 */
@Composable
fun formatDateTime(timestamp: String): String {
    return try {
        val date = Date(timestamp.toLong())
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        sdf.format(date)
    } catch (e: Exception) {
        timestamp
    }
}
