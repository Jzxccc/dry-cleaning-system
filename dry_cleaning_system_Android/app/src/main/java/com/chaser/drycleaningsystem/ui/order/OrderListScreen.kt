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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.chaser.drycleaningsystem.data.entity.Order

/**
 * 订单列表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel = viewModel(),
    onCreateOrder: () -> Unit,
    onOrderClick: (Order) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusFilter by viewModel.orderStatusFilter.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("搜索订单号") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
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
                        CircularProgressIndicator()
                    }
                }
                is OrderUiState.Success -> {
                    val orders = state.orders.filter { order ->
                        val matchesSearch = searchQuery.isBlank() ||
                                order.orderNo.contains(searchQuery, ignoreCase = true)
                        val matchesStatus = statusFilter == null || order.status == statusFilter
                        
                        matchesSearch && matchesStatus
                    }
                    
                    if (orders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无订单")
                        }
                    } else {
                        LazyColumn {
                            items(orders, key = { it.id }) { order ->
                                OrderListItem(
                                    order = order,
                                    onClick = { onOrderClick(order) }
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
            
            Text(
                text = "总价：¥${String.format("%.2f", order.totalPrice)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "支付方式：${getPayTypeText(order.payType)}",
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

/**
 * 状态徽章
 */
@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "UNWASHED" -> Color(0xFFFF9800) to "未洗"
        "WASHED" -> Color(0xFF2196F3) to "已洗"
        "FINISHED" -> Color(0xFF4CAF50) to "已取"
        else -> Color.Gray to status
    }
    
    Box(
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * 支付方式文本
 */
@Composable
fun getPayTypeText(payType: String): String {
    return when (payType) {
        "CASH" -> "现金"
        "PREPAID" -> "储值"
        "UNPAID" -> "未支付"
        else -> payType
    }
}
