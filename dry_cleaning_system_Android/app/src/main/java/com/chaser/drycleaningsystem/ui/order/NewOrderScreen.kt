package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chaser.drycleaningsystem.data.entity.Customer

/**
 * 新建订单页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    onNavigateBack: () -> Unit,
    onCreateOrder: (customerId: Long, payType: String, urgent: Boolean, clothesList: List<ClothesItem>) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var payType by remember { mutableStateOf("CASH") }
    var isUrgent by remember { mutableStateOf(false) }
    var clothesList by remember { mutableStateOf<List<ClothesItem>>(emptyList()) }
    var showCustomerSelector by remember { mutableStateOf(false) }
    var showAddClothesDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新建订单") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 客户选择
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showCustomerSelector = true }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "选择客户",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (selectedCustomer != null) {
                            Text(text = selectedCustomer!!.name)
                            Text(text = selectedCustomer!!.phone ?: "")
                            Text(
                                text = "余额：¥${String.format("%.2f", selectedCustomer!!.balance)}",
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "点击选择客户",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            // 衣物列表
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "衣物列表",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showAddClothesDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "添加衣物")
                            }
                        }
                        
                        if (clothesList.isEmpty()) {
                            Text(
                                text = "暂无衣物，点击右上角添加",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            clothesList.forEachIndexed { index, clothes ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = clothes.type)
                                        if (clothes.damageRemark != null) {
                                            Text(
                                                text = "备注：${clothes.damageRemark}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "¥${clothes.price}",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                clothesList = clothesList.filterIndexed { i, _ -> i != index }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "删除",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "总计：",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "¥${clothesList.sumOf { it.price }}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            
            // 支付方式
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "支付方式",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = payType == "CASH",
                                onClick = { payType = "CASH" },
                                label = { Text("现金") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = payType == "PREPAID",
                                onClick = { payType = "PREPAID" },
                                label = { Text("储值") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = payType == "UNPAID",
                                onClick = { payType = "UNPAID" },
                                label = { Text("未支付") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            // 加急选项
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "加急订单",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = isUrgent,
                            onCheckedChange = { isUrgent = it }
                        )
                    }
                }
            }
            
            // 提交按钮
            item {
                Button(
                    onClick = {
                        if (selectedCustomer != null && clothesList.isNotEmpty()) {
                            onCreateOrder(
                                selectedCustomer!!.id,
                                payType,
                                isUrgent,
                                clothesList
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedCustomer != null && clothesList.isNotEmpty()
                ) {
                    Text("提交订单")
                }
            }
        }
    }
    
    // 客户选择对话框
    if (showCustomerSelector) {
        CustomerSelectorDialog(
            onCustomerSelected = { customer ->
                selectedCustomer = customer
                showCustomerSelector = false
            },
            onDismiss = { showCustomerSelector = false }
        )
    }
    
    // 添加衣物对话框
    if (showAddClothesDialog) {
        AddClothesDialog(
            onClothesAdded = { type, price, damageRemark ->
                clothesList = clothesList + ClothesItem(type, price, damageRemark)
                showAddClothesDialog = false
            },
            onDismiss = { showAddClothesDialog = false }
        )
    }
}
