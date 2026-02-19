package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.ui.customer.CustomerUiState
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel

/**
 * 客户选择对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSelectorDialog(
    viewModel: CustomerViewModel = viewModel(),
    onCustomerSelected: (Customer) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择客户") },
        text = {
            when (val state = uiState) {
                is CustomerUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is CustomerUiState.Success -> {
                    LazyColumn {
                        items(state.customers, key = { it.id }) { customer ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = customer.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(text = customer.phone ?: "")
                                        Text(
                                            text = "余额：¥${String.format("%.2f", customer.balance)}",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                modifier = Modifier.clickable {
                                    onCustomerSelected(customer)
                                }
                            )
                        }
                    }
                }
                is CustomerUiState.Error -> {
                    Text("错误：${state.message}")
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
