package com.chaser.drycleaningsystem.ui.order

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.DataInjection
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel

/**
 * 新建订单页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    viewModel: OrderViewModel,
    onNavigateBack: () -> Unit,
    onCreateOrder: (customerId: Long, payType: String, urgent: Boolean, clothesList: List<ClothesItem>) -> Unit
) {
    val context = LocalContext.current
    val customerViewModel: CustomerViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CustomerViewModel(
                    repository = DataInjection.getCustomerRepository(context),
                    orderRepository = DataInjection.getOrderRepository(context),
                    rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                ) as T
            }
        }
    )
    
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var payType by remember { mutableStateOf("CASH") }
    var isUrgent by remember { mutableStateOf(false) }
    var clothesList by remember { mutableStateOf<List<ClothesItem>>(emptyList()) }
    var showCustomerSelector by remember { mutableStateOf(false) }
    var showAddClothesDialog by remember { mutableStateOf(false) }
    
    // 当选择客户后，如果客户有余额，自动设置为储值支付
    LaunchedEffect(selectedCustomer) {
        selectedCustomer?.let { customer ->
            if (customer.balance > 0) {
                payType = "PREPAID"
            } else {
                payType = "CASH"
            }
        }
    }
    
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

                        // 显示客户余额提示
                        if (selectedCustomer != null) {
                            Text(
                                text = "客户余额：¥${String.format("%.2f", selectedCustomer!!.balance)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selectedCustomer!!.balance > 0) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            if (selectedCustomer!!.balance > 0 && payType == "PREPAID") {
                                Text(
                                    text = "✓ 已自动选择储值支付",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

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
                // 计算总价
                val totalPrice = clothesList.sumOf { it.price }
                
                // 获取客户余额
                val customerBalance = selectedCustomer?.balance ?: 0.0
                
                // 检查储值支付时余额是否足够
                val isBalanceSufficient = if (payType == "PREPAID") {
                    customerBalance >= totalPrice
                } else {
                    true
                }

                Button(
                    onClick = {
                        selectedCustomer?.let { customer ->
                            if (clothesList.isNotEmpty()) {
                                onCreateOrder(
                                    customer.id,
                                    payType,
                                    isUrgent,
                                    clothesList
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedCustomer != null && clothesList.isNotEmpty() && isBalanceSufficient
                ) {
                    Text(if (payType == "PREPAID" && !isBalanceSufficient) {
                        "余额不足 (¥${String.format("%.2f", customerBalance)} < ¥${String.format("%.2f", totalPrice)})"
                    } else {
                        "提交订单"
                    })
                }

                // 余额不足提示
                if (payType == "PREPAID" && selectedCustomer != null && !isBalanceSufficient) {
                    Text(
                        text = "⚠️ 客户余额 (¥${String.format("%.2f", customerBalance)}) 不足以支付订单金额 (¥${String.format("%.2f", totalPrice)})，请选择现金支付或先充值",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
    
    // 客户选择对话框
    if (showCustomerSelector) {
        CustomerSelectorDialog(
            viewModel = customerViewModel,
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
