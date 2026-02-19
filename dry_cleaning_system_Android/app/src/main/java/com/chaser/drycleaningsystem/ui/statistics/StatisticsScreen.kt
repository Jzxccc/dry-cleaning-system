package com.chaser.drycleaningsystem.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Order
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
                    text = "支付方式：${order.payType}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "状态：${order.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (order.status) {
                        "已完成" -> MaterialTheme.colorScheme.primary
                        "待取件" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
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
    // 使用简单的日期选择
    var year by remember {
        mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR))
    }
    var month by remember {
        mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH))
    }
    var day by remember {
        mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
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
            Column {
                if (reportType == ReportType.MONTHLY) {
                    // 年月选择器
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 年滚轮
                        Text("年:")
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier
                                .width(80.dp)
                                .height(150.dp)
                        ) {
                            items(10) { index ->
                                val y = Calendar.getInstance().get(Calendar.YEAR) - 5 + index
                                Text(
                                    text = "$y 年",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    color = if (y == year) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // 月滚轮
                        Text("月:")
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier
                                .width(60.dp)
                                .height(150.dp)
                        ) {
                            items(12) { index ->
                                val m = index + 1
                                Text(
                                    text = "$m 月",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    color = if (m - 1 == month) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                } else {
                    // 简单日期输入提示
                    Text("请选择日期：${year}-${month + 1}-$day")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("使用系统日期选择器...")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    onDateSelected(calendar.timeInMillis)
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun formatDateTime(timestamp: String): String {
    return try {
        val date = Date(timestamp.toLong())
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        sdf.format(date)
    } catch (e: Exception) {
        timestamp
    }
}
