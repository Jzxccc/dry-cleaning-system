package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel

/**
 * 订单列表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel = viewModel(),
    customerViewModel: CustomerViewModel = viewModel(),
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
        topBar = {
            TopAppBar(
                title = { Text("订单管理") },
                actions = {
                    IconButton(onClick = { onCreateOrder() }) {
                        Icon(Icons.Default.Add, contentDescription = "新建订单")
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
            // 搜索框
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 搜索类型选择按钮
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = searchType == "order_no",
                        onClick = { viewModel.setSearchType("order_no") },
                        label = { Text("订单号") }
                    )
                    FilterChip(
                        selected = searchType == "customer",
                        onClick = { viewModel.setSearchType("customer") },
                        label = { Text("客户") }
                    )
                }

                // 搜索输入框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.search(it) },
                    modifier = Modifier.weight(2f),
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
                    singleLine = true
                )

                // 清除按钮
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.search("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            }
            
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
                        CircularProgressIndicator()
                    }
                }
                is OrderUiState.Success -> {
                    // 使用搜索结果或全部订单
                    val orders = if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                        // 有搜索结果时使用搜索结果
                        searchResults.filter { order ->
                            statusFilter == null || order.status == statusFilter
                        }
                    } else {
                        // 否则使用全部订单并应用筛选
                        state.orders.filter { order ->
                            val matchesStatus = statusFilter == null || order.status == statusFilter
                            matchesStatus
                        }
                    }

                    if (orders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (searchQuery.isNotBlank()) "未找到匹配的订单" else "暂无订单"
                            )
                        }
                    } else {
                        LazyColumn {
                            items(orders, key = { it.id }) { order ->
                                val customer = customerMap[order.customerId]
                                OrderListItem(
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
 * 状态筛选行
 */
@Composable
fun StatusFilterRow(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatusChip("全部", null, selectedStatus, onStatusSelected)
        StatusChip("未洗", "UNWASHED", selectedStatus, onStatusSelected)
        StatusChip("已洗", "WASHED", selectedStatus, onStatusSelected)
        StatusChip("已取", "FINISHED", selectedStatus, onStatusSelected)
    }
}

@Composable
fun StatusChip(
    label: String,
    status: String?,
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    val isSelected = selectedStatus == status

    FilterChip(
        selected = isSelected,
        onClick = { onStatusSelected(status) },
        label = { Text(label) }
    )
}

/**
 * 订单列表项
 */
@Composable
fun OrderListItem(
    order: Order,
    customerName: String = "",
    customerPhone: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.orderNo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 客户信息
            if (customerName.isNotEmpty()) {
                Text(
                    text = "客户：$customerName",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (customerPhone.isNotEmpty()) {
                    Text(
                        text = "电话：$customerPhone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "总价：¥${String.format("%.2f", order.totalPrice)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "支付方式：${OrderPayTypeText(order.payType)}",
                style = MaterialTheme.typography.bodySmall
            )

            if (order.urgent == 1) {
                Text(
                    text = "🔥 加急",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    }
}
