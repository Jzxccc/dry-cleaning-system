package com.chaser.drycleaningsystem.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 主菜单项
 */
data class MenuItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * 主菜单页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCustomers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToRecharge: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val menuItems = listOf(
        MenuItem(
            id = "customers",
            title = "客户管理",
            description = "管理客户信息和余额",
            icon = Icons.Default.People
        ),
        MenuItem(
            id = "orders",
            title = "订单管理",
            description = "创建和查看订单",
            icon = Icons.Default.Inventory2
        ),
        MenuItem(
            id = "recharge",
            title = "会员充值",
            description = "为客户进行储值充值",
            icon = Icons.Default.MonetizationOn
        ),
        MenuItem(
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
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuItems.size) { index ->
                val item = menuItems[index]
                HomeMenuItem(
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

/**
 * 主菜单项卡片
 */
@Composable
fun HomeMenuItem(
    item: MenuItem,
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
