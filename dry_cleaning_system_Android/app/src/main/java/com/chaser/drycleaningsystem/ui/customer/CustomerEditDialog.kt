package com.chaser.drycleaningsystem.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chaser.drycleaningsystem.data.entity.Customer

/**
 * 客户编辑对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerEditDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String?, wechat: String?, balance: Double, note: String?) -> Unit,
    customer: Customer? = null
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var phone by remember { mutableStateOf(customer?.phone ?: "") }
    var wechat by remember { mutableStateOf(customer?.wechat ?: "") }
    var balance by remember { mutableStateOf(customer?.balance?.toString() ?: "0") }
    var note by remember { mutableStateOf(customer?.note ?: "") }

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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("余额") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("¥") },
                    singleLine = true
                )

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
            TextButton(
                onClick = {
                    val balanceValue = balance.toDoubleOrNull() ?: 0.0
                    onConfirm(name, phone.ifBlank { null }, wechat.ifBlank { null }, balanceValue, note.ifBlank { null })
                },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
