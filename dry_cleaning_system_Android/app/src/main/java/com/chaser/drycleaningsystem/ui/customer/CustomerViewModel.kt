package com.chaser.drycleaningsystem.ui.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.data.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 客户 ViewModel
 */
class CustomerViewModel(
    private val repository: CustomerRepository,
    private val orderRepository: com.chaser.drycleaningsystem.data.repository.OrderRepository,
    private val rechargeRecordRepository: com.chaser.drycleaningsystem.data.repository.RechargeRecordRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allCustomers: Flow<List<Customer>> = repository.allCustomers

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
    
    private val _uiState = MutableStateFlow<CustomerUiState>(CustomerUiState.Loading)
    val uiState: StateFlow<CustomerUiState> = _uiState
    
    init {
        observeCustomers()
    }
    
    private fun observeCustomers() {
        viewModelScope.launch {
            allCustomers.collect { customers ->
                _uiState.value = CustomerUiState.Success(customers)
            }
        }
    }
    
    fun search(query: String) {
        _searchQuery.value = query
    }

    /**
     * 根据 ID 获取客户
     */
    fun getCustomerById(customerId: Long): Flow<Customer?> {
        return allCustomers.map { customers ->
            customers.find { it.id == customerId }
        }
    }

    fun addCustomer(name: String, phone: String?, wechat: String?, balance: Double, note: String?) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    name = name,
                    phone = phone,
                    wechat = wechat,
                    balance = balance,
                    note = note
                )
                repository.insert(customer)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateCustomer(id: Long, name: String, phone: String?, wechat: String?, balance: Double, note: String?) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    id = id,
                    name = name,
                    phone = phone,
                    wechat = wechat,
                    balance = balance,
                    note = note
                )
                repository.update(customer)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                repository.delete(customer)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 获取客户详情数据
    suspend fun getCustomerDetail(customerId: Long): CustomerDetailData? {
        Log.d("DRY CLEAN SYSTEM LOG", "getCustomerDetail 开始，customerId: $customerId")
        return try {
            val customer = repository.getCustomerById(customerId)
            Log.d("DRY CLEAN SYSTEM LOG", "获取客户信息：${customer != null}, 姓名：${customer?.name}")
            if (customer == null) return null
            
            val orders = orderRepository.getOrdersByCustomerId(customerId).first()
            Log.d("DRY CLEAN SYSTEM LOG", "获取订单列表：${orders.size} 条")
            
            val rechargeRecords = rechargeRecordRepository.getRechargeRecordsByCustomerId(customerId).first()
            Log.d("DRY CLEAN SYSTEM LOG", "获取充值记录：${rechargeRecords.size} 条")
            
            CustomerDetailData(customer, orders, rechargeRecords)
        } catch (e: Exception) {
            Log.e("DRY CLEAN SYSTEM LOG", "获取客户详情失败", e)
            null
        }
    }
}

/**
 * 客户详情数据
 */
data class CustomerDetailData(
    val customer: Customer,
    val orders: List<com.chaser.drycleaningsystem.data.entity.Order>,
    val rechargeRecords: List<com.chaser.drycleaningsystem.data.entity.RechargeRecord>
)

/**
 * 客户 UI 状态
 */
sealed class CustomerUiState {
    object Loading : CustomerUiState()
    data class Success(val customers: List<Customer>) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}
