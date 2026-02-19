package com.chaser.drycleaningsystem.ui.customer

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

/**
 * 客户 ViewModel
 */
class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {
    
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
    
    fun addCustomer(name: String, phone: String?, wechat: String?, balance: Double) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    name = name,
                    phone = phone,
                    wechat = wechat,
                    balance = balance
                )
                repository.insert(customer)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun updateCustomer(id: Long, name: String, phone: String?, wechat: String?, balance: Double) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    id = id,
                    name = name,
                    phone = phone,
                    wechat = wechat,
                    balance = balance
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
}

/**
 * 客户 UI 状态
 */
sealed class CustomerUiState {
    object Loading : CustomerUiState()
    data class Success(val customers: List<Customer>) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}
