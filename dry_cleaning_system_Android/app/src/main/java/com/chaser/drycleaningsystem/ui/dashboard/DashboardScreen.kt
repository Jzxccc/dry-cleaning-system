package com.chaser.drycleaningsystem.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.chaser.drycleaningsystem.ui.theme.Primary
import com.chaser.drycleaningsystem.ui.theme.Secondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 仪表板菜单项
 */
data class DashboardMenuItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradientColors: List<Color>
)

/**
 * 统计卡片数据
 */
data class StatCard(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val trend: String? = null,
    val trendUp: Boolean = true,
    val backgroundColor: Color
)

/**
 * 仪表板屏幕 - 现代化设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToCustomers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToRecharge: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(Date()) }
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 页面可见时刷新数据
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 根据真实数据生成统计卡片
    val stats = listOf(
        StatCard(
            title = "今日订单",
            value = if (uiState.isLoading) "..." else uiState.todayOrders.toString(),
            icon = Icons.Default.Inventory2,
            trend = null,
            trendUp = true,
            backgroundColor = Primary
        ),
        StatCard(
            title = "待处理",
            value = if (uiState.isLoading) "..." else uiState.pendingOrders.toString(),
            icon = Icons.Outlined.Schedule,
            trend = null,
            trendUp = false,
            backgroundColor = Secondary
        ),
        StatCard(
            title = "本月客户",
            value = if (uiState.isLoading) "..." else uiState.monthCustomers.toString(),
            icon = Icons.Default.People,
            trend = null,
            trendUp = true,
            backgroundColor = Color(0xFF7C4DFF)
        ),
        StatCard(
            title = "月营收",
            value = if (uiState.isLoading) "..." else "¥${String.format("%.1f", uiState.monthRevenue)}",
            icon = Icons.Outlined.TrendingUp,
            trend = null,
            trendUp = true,
            backgroundColor = Color(0xFF00C853)
        )
    )

    val menuItems = listOf(
        DashboardMenuItem(
            id = "customers",
            title = "客户管理",
            description = "管理客户信息和余额",
            icon = Icons.Default.People,
            gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))
        ),
        DashboardMenuItem(
            id = "orders",
            title = "订单管理",
            description = "创建和查看订单",
            icon = Icons.Default.Inventory2,
            gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF43A047))
        ),
        DashboardMenuItem(
            id = "recharge",
            title = "会员充值",
            description = "为客户进行储值充值",
            icon = Icons.Default.MonetizationOn,
            gradientColors = listOf(Color(0xFFFFA726), Color(0xFFFB8C00))
        ),
        DashboardMenuItem(
            id = "statistics",
            title = "统计报表",
            description = "查看日报和月报",
            icon = Icons.Default.BarChart,
            gradientColors = listOf(Color(0xFFAB47BC), Color(0xFF8E24AA))
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ModernTopAppBar(selectedDate = selectedDate)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 统计卡片区 - 使用固定行数的 Row 而不是 LazyVerticalGrid
            item {
                StatsSectionFixed(stats = stats)
            }

            // 功能入口区
            item {
                Text(
                    text = "快捷功能",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            // 功能网格 - 使用固定布局
            item {
                FunctionGridFixed(
                    items = menuItems,
                    onItemClick = { itemId ->
                        when (itemId) {
                            "customers" -> onNavigateToCustomers()
                            "orders" -> onNavigateToOrders()
                            "recharge" -> onNavigateToRecharge()
                            "statistics" -> onNavigateToStatistics()
                        }
                    }
                )
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
/**
 * 现代化顶部栏
 */
@Composable
fun ModernTopAppBar(selectedDate: Date) {
    // 获取状态栏高度
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    top = statusBarsPadding + 16.dp, // 状态栏 + 额外间距
                    end = 20.dp,
                    bottom = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "干洗店管理系统，欢迎回来",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = getCurrentDate(selectedDate),
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 统计卡片区 - 使用固定布局避免嵌套 Lazy
 */
@Composable
fun StatsSectionFixed(stats: List<StatCard>) {
    // 背景色块区域
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // 标题
            Text(
                text = "今日概览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 2x2 网格布局
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 第一列
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCardItemFlat(stat = stats[0])
                    StatCardItemFlat(stat = stats[2])
                }
                
                // 第二列
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCardItemFlat(stat = stats[1])
                    StatCardItemFlat(stat = stats[3])
                }
            }
        }
    }
}

/**
 * 扁平化统计项 - 图标在左，文字在右
 */
@Composable
fun StatCardItemFlat(
    stat: StatCard,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(270.dp)
            .height(75.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 图标（左侧）
            Icon(
                imageVector = stat.icon,
                contentDescription = null,
                modifier = Modifier.size(46.dp),
                tint = stat.backgroundColor
            )
            
            // 分割线（垂直）
            VerticalDivider(
                modifier = Modifier
                    .height(55.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            // 文字（右侧，垂直排列）
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // 数值
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                
                // 标题
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * 功能网格 - 固定版本
 */
@Composable
fun FunctionGridFixed(
    items: List<DashboardMenuItem>,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // 第一行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FunctionGridItemFixed(
                item = items[0],
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                onClick = { onItemClick(items[0].id) }
            )
            FunctionGridItemFixed(
                item = items[1],
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                onClick = { onItemClick(items[1].id) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 第二行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FunctionGridItemFixed(
                item = items[2],
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                onClick = { onItemClick(items[2].id) }
            )
            FunctionGridItemFixed(
                item = items[3],
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                onClick = { onItemClick(items[3].id) }
            )
        }
    }
}

/**
 * 功能网格项 - 固定版本
 */
@Composable
fun FunctionGridItemFixed(
    item: DashboardMenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = item.gradientColors
                    )
                )
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 图标
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp),
                        tint = Color.White
                    )
                }

                // 标题和描述
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

private fun getCurrentDate(date: Date): String {
    val sdf = SimpleDateFormat("yyyy 年 MM 月 dd 日 EEEE", Locale.CHINA)
    return sdf.format(date)
}
