package com.chaser.drycleaningsystem.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * 仪表板菜单项
 */
data class DashboardMenuItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * 仪表板屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCustomers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToRecharge: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val menuItems = listOf(
        DashboardMenuItem(
            id = "customers",
            title = "客户管理",
            description = "管理客户信息和余额",
            icon = Icons.Default.People
        ),
        DashboardMenuItem(
            id = "orders",
            title = "订单管理",
            description = "创建和查看订单",
            icon = Icons.Default.Inventory2
        ),
        DashboardMenuItem(
            id = "recharge",
            title = "会员充值",
            description = "为客户进行储值充值",
            icon = Icons.Default.MonetizationOn
        ),
        DashboardMenuItem(
            id = "statistics",
            title = "统计报表",
            description = "查看日报和月报",
            icon = Icons.Default.BarChart
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "干洗店管理系统",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Android 版",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // 显示当前日期
                        Text(
                            text = getCurrentDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 快捷功能卡片
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems.size) { index ->
                    val item = menuItems[index]
                    DashboardMenuItem(
                        item = item,
                        onClick = {
                            when (item.id) {
                                "customers" -> onNavigateToCustomers()
                                "orders" -> onNavigateToOrders()
                                "recharge" -> onNavigateToRecharge()
                                "statistics" -> onNavigateToStatistics()
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * 仪表板菜单项卡片
 */
@Composable
fun DashboardMenuItem(
    item: DashboardMenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy 年 MM 月 dd 日 EEEE", Locale.CHINA)
    return sdf.format(Date())
}
