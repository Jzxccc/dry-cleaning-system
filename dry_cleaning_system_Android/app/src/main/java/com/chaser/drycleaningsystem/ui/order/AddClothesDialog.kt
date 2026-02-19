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
    
    val selectedPrice = ClothesTypePricing.clothesTypes
        .find { it.name == selectedType }?.price ?: 0.0
    
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
                                text = { Text("${type.name} - ¥${type.price}") },
                                onClick = {
                                    selectedType = type.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 价格显示
                Text(
                    text = "价格：¥${String.format("%.2f", selectedPrice)}",
                    style = MaterialTheme.typography.bodyLarge
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
                    onClothesAdded(selectedType, selectedPrice, damageRemark.ifBlank { null })
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
