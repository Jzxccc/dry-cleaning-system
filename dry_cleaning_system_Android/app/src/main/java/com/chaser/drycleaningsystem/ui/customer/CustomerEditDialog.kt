package com.chaser.drycleaningsystem.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chaser.drycleaningsystem.data.entity.Customer

/**
 * 客户编辑对话框 - 支持创建时充值
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerEditDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String?, wechat: String?, balance: Double, note: String?) -> Unit,
    onConfirmWithRecharge: (name: String, phone: String?, wechat: String?, note: String?, rechargeAmount: Double) -> Unit,
    customer: Customer? = null
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var phone by remember { mutableStateOf(customer?.phone ?: "") }
    var wechat by remember { mutableStateOf(customer?.wechat ?: "") }
    var balance by remember { mutableStateOf(customer?.balance?.toString() ?: "0") }
    var note by remember { mutableStateOf(customer?.note ?: "") }
    
    // 充值相关状态
    var rechargeAmount by remember { mutableStateOf("") }
    var showRechargeSection by remember { mutableStateOf(customer == null) } // 仅新建客户显示充值选项
    var showConfirmRechargeDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 计算充值金额（阶梯比例：100 送 10%，200 送 20%）
    val rechargeValue = rechargeAmount.toDoubleOrNull() ?: 0.0
    val giftAmount = if (rechargeValue >= 200) {
        rechargeValue * 0.2
    } else if (rechargeValue >= 100) {
        rechargeValue * 0.1
    } else {
        0.0
    }
    val totalAmount = rechargeValue + giftAmount
    val isValidRecharge = rechargeValue > 0 && rechargeValue.toInt() % 100 == 0

    AlertDialog(
        onDismissRequest = { }, // 禁用点击空白关闭
        title = {
            Text(if (customer == null) "添加客户" else "编辑客户")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名 *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("手机号") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = wechat,
                    onValueChange = { wechat = it },
                    label = { Text("微信") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 充值选项（仅新建客户时显示）
                if (customer == null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 充值开关
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
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
                                    text = "创建并充值",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "充 100 送 10%，充 200 送 20%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = showRechargeSection,
                                onCheckedChange = { showRechargeSection = it }
                            )
                        }
                    }

                    // 充值金额输入
                    if (showRechargeSection) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // 快捷充值按钮
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(100, 200, 500, 1000).forEach { amount ->
                                FilterChip(
                                    selected = rechargeAmount == amount.toString(),
                                    onClick = { rechargeAmount = amount.toString() },
                                    label = { Text("¥$amount") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rechargeAmount,
                            onValueChange = { rechargeAmount = it },
                            label = { Text("充值金额") },
                            modifier = Modifier.fillMaxWidth(),
                            prefix = { Text("¥") },
                            placeholder = { Text("100 的整数倍") },
                            singleLine = true,
                            isError = rechargeAmount.isNotEmpty() && !isValidRecharge
                        )

                        if (rechargeAmount.isNotEmpty() && !isValidRecharge) {
                            Text(
                                text = "充值金额必须是 100 的整数倍",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // 充值计算信息
                        if (rechargeValue > 0) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "充值金额：",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "¥${String.format("%.2f", rechargeValue)}",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "赠送金额：",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "¥${String.format("%.2f", giftAmount)}",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            text = "实际到账：",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "¥${String.format("%.2f", totalAmount)}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 编辑客户时显示余额
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = balance,
                        onValueChange = { balance = it },
                        label = { Text("余额") },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("¥") },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            if (showRechargeSection && rechargeAmount.isNotEmpty()) {
                // 有充值金额时显示"创建并充值"按钮
                TextButton(
                    onClick = {
                        if (!isValidRecharge) {
                            errorMessage = "充值金额必须是 100 的整数倍"
                            return@TextButton
                        }
                        showConfirmRechargeDialog = true
                    },
                    enabled = name.isNotBlank() && isValidRecharge
                ) {
                    Text("创建并充值")
                }
            } else {
                // 普通保存按钮
                TextButton(
                    onClick = {
                        val balanceValue = balance.toDoubleOrNull() ?: 0.0
                        onConfirm(name, phone.ifBlank { null }, wechat.ifBlank { null }, balanceValue, note.ifBlank { null })
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("保存")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )

    // 充值确认对话框
    if (showConfirmRechargeDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmRechargeDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("确认充值") },
            text = {
                Column {
                    Text("即将为客户\"$name\"进行充值：")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("充值金额：", style = MaterialTheme.typography.bodyMedium)
                        Text("¥${String.format("%.2f", rechargeValue)}", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("赠送金额：", style = MaterialTheme.typography.bodyMedium)
                        Text("¥${String.format("%.2f", giftAmount)}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("实际到账：", fontWeight = FontWeight.Bold)
                        Text("¥${String.format("%.2f", totalAmount)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmWithRecharge(name, phone.ifBlank { null }, wechat.ifBlank { null }, note.ifBlank { null }, rechargeValue)
                        showConfirmRechargeDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmRechargeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 错误提示对话框
    errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("提示") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("确定")
                }
            }
        )
    }
}
