package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 添加衣物对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothesDialog(
    onClothesAdded: (type: String, price: Double, damageRemark: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(ClothesTypePricing.clothesTypes.first().name) }
    var damageRemark by remember { mutableStateOf("") }
    
    // 获取基础价格
    val basePrice = ClothesTypePricing.clothesTypes
        .find { it.name == selectedType }?.price ?: 0.0
    
    // 可调整的价格
    var customPrice by remember { mutableStateOf(basePrice.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加衣物") },
        text = {
            Column {
                // 衣物类型选择
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("衣物类型") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ClothesTypePricing.clothesTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text("${type.name} - 基础价¥${type.price}") },
                                onClick = {
                                    selectedType = type.name
                                    customPrice = type.price.toString()
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 价格输入（可调整）
                OutlinedTextField(
                    value = customPrice,
                    onValueChange = { customPrice = it },
                    label = { Text("价格（可调整）") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("¥") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "基础价：¥${String.format("%.2f", basePrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 破损备注
                OutlinedTextField(
                    value = damageRemark,
                    onValueChange = { damageRemark = it },
                    label = { Text("破损备注（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = customPrice.toDoubleOrNull() ?: basePrice
                    onClothesAdded(selectedType, price, damageRemark.ifBlank { null })
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
