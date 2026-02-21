package com.chaser.drycleaningsystem.ui.order

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.chaser.drycleaningsystem.data.entity.Clothes
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel
import com.chaser.drycleaningsystem.utils.CameraHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: OrderViewModel,
    orderId: Long,
    customerName: String,
    customerPhone: String?,
    onNavigateBack: () -> Unit
) {
    // 获取订单详情
    val orderDetail = remember { viewModel.getOrderDetail(orderId) }
    val order = orderDetail?.order
    val clothesList = orderDetail?.clothesList ?: emptyList()

    // 观察当前订单状态变化
    val currentOrder by viewModel.currentOrder.collectAsState(initial = order)
    val displayOrder = currentOrder ?: order ?: return
    
    // 添加调试日志
    LaunchedEffect(displayOrder.id) {
        Log.d("DRY CLEAN SYSTEM LOG", "========== 订单详情调试信息 ==========")
        Log.d("DRY CLEAN SYSTEM LOG", "订单 ID: ${displayOrder.id}")
        Log.d("DRY CLEAN SYSTEM LOG", "订单号：${displayOrder.orderNo}")
        Log.d("DRY CLEAN SYSTEM LOG", "照片路径：${displayOrder.photoPath ?: "null"}")
        
        // 检查照片文件是否存在
        if (!displayOrder.photoPath.isNullOrBlank()) {
            val photoFile = File(displayOrder.photoPath)
            Log.d("DRY CLEAN SYSTEM LOG", "照片文件存在：${photoFile.exists()}")
            Log.d("DRY CLEAN SYSTEM LOG", "照片文件路径：${photoFile.absolutePath}")
            
            // 检查缩略图
            val thumbnailPath = displayOrder.photoPath.replace(".jpg", "_thumb.jpg")
            val thumbnailFile = File(thumbnailPath)
            Log.d("DRY CLEAN SYSTEM LOG", "缩略图文件存在：${thumbnailFile.exists()}")
            Log.d("DRY CLEAN SYSTEM LOG", "缩略图文件路径：${thumbnailFile.absolutePath}")
        }
        Log.d("DRY CLEAN SYSTEM LOG", "==========================================")
    }
    
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteSuccessDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showPaymentConfirmDialog by remember { mutableStateOf(false) }
    var pendingStatusChange by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 删除按钮
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除订单",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    // 付款按钮（仅未付款状态显示）
                    if (displayOrder.payType == "UNPAID") {
                        IconButton(onClick = { showPaymentDialog = true }) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "付款",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "付款",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        // 变更状态按钮（已付款状态显示）
                        IconButton(onClick = { showStatusDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "变更状态"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                OrderInfoCard(
                    order = displayOrder,
                    customerName = customerName,
                    customerPhone = customerPhone
                )
            }

            // 照片显示区域
            item {
                // 检查订单是否有照片路径
                if (!displayOrder.photoPath.isNullOrBlank()) {
                    val context = LocalContext.current
                    val cameraHelper = remember { CameraHelper(context) }
                    // 从数据库中的照片路径获取缩略图列表
                    val photoPaths = remember(displayOrder.photoPath) {
                        // 尝试获取同目录下的所有缩略图
                        val photoDir = displayOrder.photoPath.substringBeforeLast("/")
                        cameraHelper.getOrderThumbnails(displayOrder.id)
                    }
                    
                    Text(
                        text = "衣物照片",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    if (photoPaths.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(photoPaths.size) { index ->
                                val photoPath = photoPaths[index]

                                Box(
                                    modifier = Modifier.size(100.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.small,
                                        shadowElevation = 2.dp
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = "file://$photoPath"
                                            ),
                                            contentDescription = "衣物照片",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // 数据库有路径但文件不存在，显示提示
                        Text(
                            text = "照片文件不存在",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Text(
                    text = "衣物明细",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (clothesList.isEmpty()) {
                item {
                    Text(
                        text = "暂无衣物",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(clothesList) { clothes ->
                    ClothesItem(clothes = clothes)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showStatusDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("变更订单状态")
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("删除订单")
                }
            }
        }
    }

    if (showStatusDialog) {
        StatusChangeDialog(
            currentStatus = displayOrder.status,
            onStatusSelected = { newStatus ->
                // 如果是未付款订单且要改为已取，弹出付款确认
                if (displayOrder.payType == "UNPAID" && newStatus == "FINISHED") {
                    pendingStatusChange = newStatus
                    showPaymentConfirmDialog = true
                } else {
                    viewModel.updateOrderStatusSync(displayOrder.id, newStatus)
                }
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }
    
    // 付款确认对话框（未付款订单变更为已取时）
    if (showPaymentConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentConfirmDialog = false },
            title = { Text("订单付款确认") },
            text = {
                Column {
                    Text("订单金额：¥${String.format("%.2f", displayOrder.totalPrice)}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("订单状态将变更为'已取'，请选择付款方式：")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 已付款，处理付款并更新状态
                        viewModel.processPayment(
                            orderId = displayOrder.id,
                            customerId = displayOrder.customerId,
                            amount = displayOrder.totalPrice,
                            payType = "PREPAID",
                            onSuccess = {
                                showPaymentConfirmDialog = false
                                // 刷新当前订单数据
                                viewModel.refreshCurrentOrder(displayOrder.id)
                                pendingStatusChange?.let { status ->
                                    viewModel.updateOrderStatusSync(displayOrder.id, status)
                                    pendingStatusChange = null
                                }
                            },
                            onError = { 
                                // 储值失败，改用现金
                                viewModel.processPayment(
                                    orderId = displayOrder.id,
                                    customerId = displayOrder.customerId,
                                    amount = displayOrder.totalPrice,
                                    payType = "CASH",
                                    onSuccess = {
                                        showPaymentConfirmDialog = false
                                        // 刷新当前订单数据
                                        viewModel.refreshCurrentOrder(displayOrder.id)
                                        pendingStatusChange?.let { status ->
                                            viewModel.updateOrderStatusSync(displayOrder.id, status)
                                            pendingStatusChange = null
                                        }
                                    },
                                    onError = { /* 错误处理 */ }
                                )
                            }
                        )
                    }
                ) {
                    Text("已付款")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // 未付款，直接更新状态
                        showPaymentConfirmDialog = false
                        pendingStatusChange?.let { status ->
                            viewModel.updateOrderStatusSync(displayOrder.id, status)
                            pendingStatusChange = null
                        }
                    }
                ) {
                    Text("未付款")
                }
            }
        )
    }

    // 付款对话框（直接点击付款按钮）
    if (showPaymentDialog) {
        val context = LocalContext.current
        val customerViewModel: CustomerViewModel = viewModel(
            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CustomerViewModel(
                        repository = com.chaser.drycleaningsystem.data.DataInjection.getCustomerRepository(context),
                        orderRepository = com.chaser.drycleaningsystem.data.DataInjection.getOrderRepository(context),
                        rechargeRecordRepository = com.chaser.drycleaningsystem.data.DataInjection.getRechargeRecordRepository(context)
                    ) as T
                }
            }
        )
        
        val customer by customerViewModel.getCustomerById(displayOrder.customerId).collectAsState(initial = null)
        
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("订单付款") },
            text = {
                Column {
                    Text("订单金额：¥${String.format("%.2f", displayOrder.totalPrice)}")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    customer?.let {
                        Text("客户余额：¥${String.format("%.2f", it.balance)}")
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (it.balance >= displayOrder.totalPrice) {
                            Text(
                                text = "✓ 余额充足，可使用储值支付",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "⚠ 余额不足，请使用现金支付",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("请选择付款方式：")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.processPayment(
                            orderId = displayOrder.id,
                            customerId = displayOrder.customerId,
                            amount = displayOrder.totalPrice,
                            payType = "PREPAID",
                            onSuccess = {
                                showPaymentDialog = false
                                viewModel.refreshCurrentOrder(displayOrder.id)
                            },
                            onError = { /* 错误处理 */ }
                        )
                    },
                    enabled = customer?.balance ?: 0.0 >= displayOrder.totalPrice
                ) {
                    Text("储值支付")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.processPayment(
                            orderId = displayOrder.id,
                            customerId = displayOrder.customerId,
                            amount = displayOrder.totalPrice,
                            payType = "CASH",
                            onSuccess = {
                                showPaymentDialog = false
                                viewModel.refreshCurrentOrder(displayOrder.id)
                            },
                            onError = { /* 错误处理 */ }
                        )
                    }
                ) {
                    Text("现金支付")
                }
            }
        )
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("确认删除") },
            text = {
                Column {
                    Text("确定要删除订单 ${displayOrder.orderNo} 吗？")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "删除后无法恢复，订单衣物信息也将被删除。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // 如果是储值支付且未完成，显示退款提示
                    if (displayOrder.payType == "PREPAID" && displayOrder.status != "FINISHED") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💰 订单金额 ¥${String.format("%.2f", displayOrder.totalPrice)} 将退还到客户余额",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder(
                            orderId = displayOrder.id,
                            onSuccess = {
                                showDeleteDialog = false
                                showDeleteSuccessDialog = true
                            },
                            onError = { /* 错误处理 */ }
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
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
    
    // 删除成功提示
    if (showDeleteSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteSuccessDialog = false
                onNavigateBack()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("删除成功") },
            text = { Text("订单已成功删除") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
fun OrderInfoCard(
    order: Order,
    customerName: String,
    customerPhone: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "订单号",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.orderNo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "客户",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = customerName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (!customerPhone.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "电话：$customerPhone",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "订单状态",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "总价",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "¥${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "支付方式",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = OrderPayTypeText(order.payType),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "加急",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (order.urgent == 1) "是" else "否",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (order.urgent == 1) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "创建时间：${formatDateTime(order.createTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ClothesItem(clothes: Clothes) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clothes.type,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (!clothes.damageRemark.isNullOrBlank()) {
                    Text(
                        text = "备注：${clothes.damageRemark}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "¥${String.format("%.2f", clothes.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatusChangeDialog(
    currentStatus: String,
    onStatusSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("变更订单状态") },
        text = {
            Column {
                Text(
                    text = "请选择新的订单状态：",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                val statusOptions = listOf(
                    "UNWASHED" to "未洗",
                    "WASHED" to "已洗",
                    "FINISHED" to "已取"
                )
                
                statusOptions.forEach { (status, displayName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelected(status) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (currentStatus == status) {
                            AssistChip(
                                onClick = { },
                                label = { Text("当前") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
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

@Composable
fun formatDateTime(dateStr: String): String {
    return try {
        val date = Date(dateStr.toLong())
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.format(date)
    } catch (e: Exception) {
        dateStr
    }
}

/**
 * 付款对话框
 */
@Composable
fun PaymentDialog(
    amount: Double,
    customerId: Long,
    orderId: Long,
    onPaymentComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val orderViewModel: OrderViewModel = viewModel()
    val customerViewModel: CustomerViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CustomerViewModel(
                    repository = com.chaser.drycleaningsystem.data.DataInjection.getCustomerRepository(context),
                    orderRepository = com.chaser.drycleaningsystem.data.DataInjection.getOrderRepository(context),
                    rechargeRecordRepository = com.chaser.drycleaningsystem.data.DataInjection.getRechargeRecordRepository(context)
                ) as T
            }
        }
    )
    
    val customer by customerViewModel.getCustomerById(customerId).collectAsState(initial = null)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("订单付款") },
        text = {
            Column {
                Text("订单金额：¥${String.format("%.2f", amount)}")
                Spacer(modifier = Modifier.height(16.dp))
                
                customer?.let {
                    Text("客户余额：¥${String.format("%.2f", it.balance)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (it.balance >= amount) {
                        Text(
                            text = "✓ 余额充足，可使用储值支付",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "⚠ 余额不足，请使用现金支付",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("请选择付款方式：")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    orderViewModel.processPayment(
                        orderId = orderId,
                        customerId = customerId,
                        amount = amount,
                        payType = "PREPAID",
                        onSuccess = onPaymentComplete,
                        onError = { /* 错误处理 */ }
                    )
                },
                enabled = customer?.balance ?: 0.0 >= amount
            ) {
                Text("储值支付")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    orderViewModel.processPayment(
                        orderId = orderId,
                        customerId = customerId,
                        amount = amount,
                        payType = "CASH",
                        onSuccess = onPaymentComplete,
                        onError = { /* 错误处理 */ }
                    )
                }
            ) {
                Text("现金支付")
            }
        }
    )
}
