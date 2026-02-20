package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.People
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
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel
import com.chaser.drycleaningsystem.ui.theme.Primary

/**
 * 订单列表页面 - 现代化设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel = viewModel(),
    customerViewModel: CustomerViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onCreateOrder: () -> Unit,
    onOrderClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusFilter by viewModel.orderStatusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchType by viewModel.searchType.collectAsState()

    // 获取所有客户用于映射
    val allCustomers by customerViewModel.allCustomers.collectAsState(initial = emptyList())
    val customerMap = remember(allCustomers) {
        allCustomers.associateBy { it.id }
    }

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
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("订单管理")
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
                        onClick = onCreateOrder,
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
                            Text("新建订单", style = MaterialTheme.typography.labelLarge, color = Color.White)
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
            SearchSection(
                searchQuery = searchQuery,
                searchType = searchType,
                onSearchQueryChange = { viewModel.search(it) },
                onSearchTypeChange = { viewModel.setSearchType(it) }
            )

            // 状态筛选
            StatusFilterRow(
                selectedStatus = statusFilter,
                onStatusSelected = { viewModel.filterByStatus(it) }
            )

            // 订单列表
            when (val state = uiState) {
                is OrderUiState.Loading -> {
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
                                text = "加载中...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is OrderUiState.Success -> {
                    // 使用搜索结果或全部订单
                    val orders = if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                        searchResults.filter { order ->
                            statusFilter == null || order.status == statusFilter
                        }
                    } else {
                        state.orders.filter { order ->
                            val matchesStatus = statusFilter == null || order.status == statusFilter
                            matchesStatus
                        }
                    }

                    if (orders.isEmpty()) {
                        EmptyOrderState(searchQuery.isNotBlank())
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(orders, key = { it.id }) { order ->
                                val customer = customerMap[order.customerId]
                                ModernOrderListItem(
                                    order = order,
                                    customerName = customer?.name ?: "",
                                    customerPhone = customer?.phone ?: "",
                                    onClick = { onOrderClick(order.id) }
                                )
                            }
                        }
                    }
                }
                is OrderUiState.Error -> {
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
 * 搜索区域
 */
@Composable
fun SearchSection(
    searchQuery: String,
    searchType: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchTypeChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 搜索类型选择
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = searchType == "order_no",
                    onClick = { onSearchTypeChange("order_no") },
                    label = { Text("订单号", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = if (searchType == "order_no") {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
                FilterChip(
                    selected = searchType == "customer",
                    onClick = { onSearchTypeChange("customer") },
                    label = { Text("客户", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = if (searchType == "customer") {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 搜索输入框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        when (searchType) {
                            "customer" -> "搜索客户名称或手机号"
                            else -> "搜索订单号"
                        }
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

/**
 * 状态筛选行
 */
@Composable
fun StatusFilterRow(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModernStatusChip("全部", null, selectedStatus, onStatusSelected)
            ModernStatusChip("未洗", "UNWASHED", selectedStatus, onStatusSelected)
            ModernStatusChip("已洗", "WASHED", selectedStatus, onStatusSelected)
            ModernStatusChip("已取", "FINISHED", selectedStatus, onStatusSelected)
        }
    }
}

@Composable
fun ModernStatusChip(
    label: String,
    status: String?,
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    val isSelected = selectedStatus == status
    val (bgColor, textColor) = when (status) {
        "UNWASHED" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "WASHED" -> Color(0xFFE3F2FD) to Color(0xFF0277BD)
        "FINISHED" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        else -> Color.Transparent to MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onStatusSelected(status) }
            .then(
                if (isSelected) {
                    Modifier.background(bgColor)
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            ),
        color = Color.Transparent
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) textColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 空状态
 */
@Composable
fun EmptyOrderState(hasSearch: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = if (hasSearch) "未找到匹配的订单" else "暂无订单",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 现代化订单列表项
 */
@Composable
fun ModernOrderListItem(
    order: Order,
    customerName: String = "",
    customerPhone: String = "",
    onClick: () -> Unit
) {
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
            // 第一行：订单号和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderNo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ModernStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 第二行：客户信息
            if (customerName.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (customerPhone.isNotEmpty()) {
                    Text(
                        text = customerPhone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 第三行：价格和支付方式
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "总价:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "¥${String.format("%.2f", order.totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = OrderPayTypeText(order.payType),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (order.urgent == 1) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "🔥 加急",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 现代化状态徽章
 */
@Composable
fun ModernStatusBadge(status: String) {
    val result = when (status) {
        "UNWASHED" -> Triple(Color(0xFFE65100), "未洗", Color(0xFFFFF3E0))
        "WASHED" -> Triple(Color(0xFF0277BD), "已洗", Color(0xFFE3F2FD))
        "FINISHED" -> Triple(Color(0xFF2E7D32), "已取", Color(0xFFE8F5E9))
        else -> Triple(Color.Gray, status, Color.LightGray)
    }
    val (color, text, bgColor) = result

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
