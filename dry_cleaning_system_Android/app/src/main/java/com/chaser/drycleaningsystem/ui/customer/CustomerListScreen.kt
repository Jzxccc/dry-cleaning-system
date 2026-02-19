package com.chaser.drycleaningsystem.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Customer

/**
 * 客户列表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    viewModel: CustomerViewModel = viewModel(),
    onAddCustomer: () -> Unit,
    onEditCustomer: (Customer) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("客户管理") },
                actions = {
                    IconButton(onClick = { onAddCustomer() }) {
                        Icon(Icons.Default.Add, contentDescription = "添加客户")
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
                placeholder = { Text("搜索客户姓名或手机号") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
            )
            
            // 客户列表
            when (val state = uiState) {
                is CustomerUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CustomerUiState.Success -> {
                    val customers = state.customers.filter {
                        searchQuery.isBlank() ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        (it.phone?.contains(searchQuery) == true)
                    }
                    
                    if (customers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无客户")
                        }
                    } else {
                        LazyColumn {
                            items(customers, key = { it.id }) { customer ->
                                CustomerListItem(
                                    customer = customer,
                                    onEdit = { onEditCustomer(customer) },
                                    onDelete = { viewModel.deleteCustomer(customer) }
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
 * 客户列表项
 */
@Composable
fun CustomerListItem(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = customer.phone ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "余额：¥${String.format("%.2f", customer.balance)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除客户\"${customer.name}\"吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
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
