package com.chaser.drycleaningsystem.ui.recharge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.data.entity.RechargeRecord
import java.text.SimpleDateFormat
import java.util.*

/**
 * 充值页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    viewModel: RechargeViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val rechargeAmount by viewModel.rechargeAmount.collectAsState()
    val recentRecords by viewModel.recentRecords.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showCustomerSelector by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("会员充值") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Clear, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 客户选择
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "选择客户",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedCustomer != null) {
                        // 已选择客户
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = selectedCustomer!!.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "手机号：${selectedCustomer!!.phone ?: "无"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "当前余额：¥${String.format("%.2f", selectedCustomer!!.balance)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { viewModel.clearSelectedCustomer() }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除选择")
                            }
                        }
                    } else {
                        // 未选择客户
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("搜索客户姓名或手机号") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            singleLine = true,
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showCustomerSelector = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("选择客户")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 充值金额输入
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "充值金额",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rechargeAmount,
                        onValueChange = { viewModel.setRechargeAmount(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入充值金额（100 的整数倍）") },
                        prefix = { Text("¥") },
                        singleLine = true
                    )

                    // 充值计算信息
                    if (rechargeAmount.isNotBlank()) {
                        val amount = rechargeAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val giftAmount = viewModel.calculateGiftAmount(amount)
                            val totalAmount = viewModel.calculateTotalAmount(amount)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "充值金额：¥${String.format("%.2f", amount)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "赠送金额：¥${String.format("%.2f", giftAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "实际到账：¥${String.format("%.2f", totalAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.submitRecharge(
                                onSuccess = { showSuccessDialog = true },
                                onError = { errorMessage = it }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedCustomer != null && rechargeAmount.isNotBlank()
                    ) {
                        Text("确认充值")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 最近充值记录
            Text(
                text = "最近充值记录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (recentRecords.isEmpty()) {
                Text(
                    text = "暂无充值记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn {
                    items(recentRecords, key = { it.id }) { record ->
                        RechargeRecordItem(record = record)
                    }
                }
            }
        }
    }

    // 客户选择对话框
    if (showCustomerSelector) {
        CustomerSelectorDialog(
            viewModel = viewModel,
            onCustomerSelected = { customer ->
                viewModel.selectCustomer(customer)
                showCustomerSelector = false
            },
            onDismiss = { showCustomerSelector = false }
        )
    }

    // 成功对话框
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("充值成功") },
            text = { Text("充值已完成，客户余额已更新。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }

    // 错误提示
    errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("充值失败") },
            text = { Text(error) },
            confirmButton = {
                TextButton(
                    onClick = { errorMessage = null }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

/**
 * 充值记录列表项
 */
@Composable
fun RechargeRecordItem(record: RechargeRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "充值 ¥${String.format("%.2f", record.rechargeAmount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "赠送 ¥${String.format("%.2f", record.giftAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = formatDate(record.createTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "+¥${String.format("%.2f", record.rechargeAmount + record.giftAmount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 客户选择对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSelectorDialog(
    viewModel: RechargeViewModel,
    onCustomerSelected: (Customer) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择客户") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.search(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("搜索客户姓名或手机号") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                when (val state = uiState) {
                    is RechargeUiState.Loading -> {
                        Box(
                            modifier = Modifier.height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is RechargeUiState.Success -> {
                        val customers = state.customers.filter {
                            searchQuery.isBlank() ||
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            (it.phone?.contains(searchQuery) == true)
                        }

                        if (customers.isEmpty()) {
                            Box(
                                modifier = Modifier.height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("暂无客户")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.height(300.dp)
                            ) {
                                items(customers, key = { it.id }) { customer ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onCustomerSelected(customer) }
                                            .padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = customer.name,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = customer.phone ?: "",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Text(
                                            text = "余额：¥${String.format("%.2f", customer.balance)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is RechargeUiState.Error -> {
                        Box(
                            modifier = Modifier.height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("错误：${state.message}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun formatDate(timestamp: String): String {
    return try {
        val date = Date(timestamp.toLong())
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.format(date)
    } catch (e: Exception) {
        timestamp
    }
}
