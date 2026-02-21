package com.chaser.drycleaningsystem.ui.order

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.DataInjection
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.ui.customer.CustomerEditDialog
import com.chaser.drycleaningsystem.ui.customer.CustomerUiState
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel

/**
 * 客户选择对话框 - 带搜索过滤和新增客户功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSelectorDialog(
    viewModel: CustomerViewModel,
    onCustomerSelected: (Customer) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAutoSelect by remember { mutableStateOf(false) }
    var newCustomerPhone by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("选择客户")
                    // 新增客户按钮
                    IconButton(
                        onClick = { showAddCustomerDialog = true }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "新增客户",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "新增",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    placeholder = { Text("搜索客户姓名或手机号") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除")
                            }
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    shape = MaterialTheme.shapes.small
                )
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                when (val state = uiState) {
                    is CustomerUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    is CustomerUiState.Success -> {
                        // 过滤客户列表
                        val filteredCustomers = state.customers.filter { customer ->
                            searchQuery.isBlank() ||
                            customer.name.contains(searchQuery, ignoreCase = true) ||
                            (customer.phone?.contains(searchQuery) == true)
                        }

                        if (filteredCustomers.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "😕",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        text = if (searchQuery.isBlank()) "暂无客户" else "未找到匹配的客户",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn {
                                items(filteredCustomers, key = { it.id }) { customer ->
                                    CustomerSelectorItem(
                                        customer = customer,
                                        onClick = { onCustomerSelected(customer) }
                                    )
                                }
                            }
                        }
                    }
                    is CustomerUiState.Error -> {
                        Text("错误：${state.message}")
                    }
                }
            }
        },
        confirmButton = {
            // 不显示确认按钮，只保留关闭按钮
        }
    )
    
    // 新增客户对话框
    if (showAddCustomerDialog) {
        CustomerEditDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone, wechat, balance, note ->
                viewModel.addCustomer(name, phone, wechat, balance, note)
                showAddCustomerDialog = false
                // 保存新客户手机号，用于自动选择
                newCustomerPhone = phone
                showAutoSelect = true
            },
            customer = null
        )
    }
    
    // 自动选择新客户
    if (showAutoSelect && newCustomerPhone != null) {
        val allCustomers by viewModel.allCustomers.collectAsState(initial = emptyList())
        val newCustomer = allCustomers.find { it.phone == newCustomerPhone }
        
        LaunchedEffect(newCustomer) {
            if (newCustomer != null) {
                onCustomerSelected(newCustomer)
                showAutoSelect = false
                newCustomerPhone = null
            }
        }
    }
}

/**
 * 客户选择项
 */
@Composable
fun CustomerSelectorItem(
    customer: Customer,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = customer.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "¥${String.format("%.2f", customer.balance)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (!customer.phone.isNullOrBlank()) {
                Text(
                    text = customer.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!customer.wechat.isNullOrBlank()) {
                Text(
                    text = "微信：${customer.wechat}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    HorizontalDivider()
}
