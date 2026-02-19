package com.chaser.drycleaningsystem.ui.recharge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.data.entity.RechargeRecord
import com.chaser.drycleaningsystem.data.repository.CustomerRepository
import com.chaser.drycleaningsystem.data.repository.RechargeRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 充值 ViewModel
 */
class RechargeViewModel(
    private val customerRepository: CustomerRepository,
    private val rechargeRecordRepository: RechargeRecordRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allCustomers: Flow<List<Customer>> = customerRepository.allCustomers

    val searchResults: Flow<List<Customer>> = _searchQuery
        .catch { e -> emit("") }
        .map { query ->
            if (query.isBlank()) {
                allCustomers.first()
            } else {
                allCustomers.first().filter { customer ->
                    customer.name.contains(query, ignoreCase = true) ||
                    (customer.phone?.contains(query) == true)
                }
            }
        }

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer

    private val _rechargeAmount = MutableStateFlow("")
    val rechargeAmount: StateFlow<String> = _rechargeAmount

    private val _uiState = MutableStateFlow<RechargeUiState>(RechargeUiState.Loading)
    val uiState: StateFlow<RechargeUiState> = _uiState

    private val _recentRecords = MutableStateFlow<List<RechargeRecord>>(emptyList())
    val recentRecords: StateFlow<List<RechargeRecord>> = _recentRecords

    init {
        observeCustomers()
        observeRecentRecords()
    }

    private fun observeCustomers() {
        viewModelScope.launch {
            allCustomers.collect { customers ->
                if (_uiState.value is RechargeUiState.Loading) {
                    _uiState.value = RechargeUiState.Success(customers)
                }
            }
        }
    }

    private fun observeRecentRecords() {
        viewModelScope.launch {
            rechargeRecordRepository.getAllRechargeRecords().collect { records ->
                _recentRecords.value = records.take(10)
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun clearSelectedCustomer() {
        _selectedCustomer.value = null
    }

    fun setRechargeAmount(amount: String) {
        _rechargeAmount.value = amount
    }

    fun submitRecharge(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val customer = _selectedCustomer.value
                val amountStr = _rechargeAmount.value

                if (customer == null) {
                    onError("请选择客户")
                    return@launch
                }

                if (amountStr.isBlank()) {
                    onError("请输入充值金额")
                    return@launch
                }

                val amount = amountStr.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    onError("请输入有效的充值金额")
                    return@launch
                }

                if (amount % 100 != 0.0) {
                    onError("充值金额必须是 100 的整数倍")
                    return@launch
                }

                // 计算赠送金额 (充 100 送 20%)
                val giftAmount = amount * 0.2
                val totalAmount = amount + giftAmount

                // 创建充值记录
                val record = RechargeRecord(
                    customerId = customer.id,
                    rechargeAmount = amount,
                    giftAmount = giftAmount
                )
                rechargeRecordRepository.insert(record)

                // 更新客户余额
                val updatedCustomer = customer.copy(balance = customer.balance + totalAmount)
                customerRepository.update(updatedCustomer)

                // 重置输入
                _selectedCustomer.value = null
                _rechargeAmount.value = ""

                onSuccess()
            } catch (e: Exception) {
                onError("充值失败：${e.message}")
            }
        }
    }

    /**
     * 计算赠送金额
     */
    fun calculateGiftAmount(amount: Double): Double {
        if (amount <= 0) return 0.0
        return amount * 0.2
    }

    /**
     * 计算实际到账金额
     */
    fun calculateTotalAmount(amount: Double): Double {
        if (amount <= 0) return 0.0
        return amount + calculateGiftAmount(amount)
    }
}

/**
 * 充值 UI 状态
 */
sealed class RechargeUiState {
    object Loading : RechargeUiState()
    data class Success(val customers: List<Customer>) : RechargeUiState()
    data class Error(val message: String) : RechargeUiState()
}
