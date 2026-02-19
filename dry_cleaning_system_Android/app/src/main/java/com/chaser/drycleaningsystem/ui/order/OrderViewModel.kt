package com.chaser.drycleaningsystem.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaser.drycleaningsystem.data.entity.Clothes
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.data.repository.ClothesRepository
import com.chaser.drycleaningsystem.data.repository.CustomerRepository
import com.chaser.drycleaningsystem.data.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单 ViewModel
 */
class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val clothesRepository: ClothesRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    val allOrders: Flow<List<Order>> = orderRepository.allOrders
    
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState
    
    private val _orderStatusFilter = MutableStateFlow<String?>(null)
    val orderStatusFilter: StateFlow<String?> = _orderStatusFilter
    
    init {
        observeOrders()
    }
    
    private fun observeOrders() {
        viewModelScope.launch {
            allOrders.collect { orders ->
                _uiState.value = OrderUiState.Success(orders)
            }
        }
    }
    
    fun search(query: String) {
        _searchQuery.value = query
    }
    
    fun filterByStatus(status: String?) {
        _orderStatusFilter.value = status
    }
    
    fun createOrder(
        customerId: Long,
        payType: String,
        urgent: Boolean,
        clothesList: List<ClothesItem>
    ) {
        viewModelScope.launch {
            try {
                // 生成订单号
                val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val dateStr = dateFormat.format(Date())
                val orderNo = "ORD-$dateStr-${System.currentTimeMillis() % 10000}"
                
                // 计算总价
                val totalPrice = clothesList.sumOf { it.price }
                
                // 创建订单
                val order = Order(
                    orderNo = orderNo,
                    customerId = customerId,
                    totalPrice = totalPrice,
                    payType = payType,
                    urgent = if (urgent) 1 else 0,
                    status = "UNWASHED"
                )
                
                val orderId = orderRepository.insert(order)
                
                // 添加衣物
                clothesList.forEach { clothesItem ->
                    val clothes = Clothes(
                        orderId = orderId.toString(),
                        type = clothesItem.type,
                        price = clothesItem.price,
                        status = "UNWASHED"
                    )
                    clothesRepository.insert(clothes)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val order = orderRepository.getOrderById(orderId)
                order?.let {
                    orderRepository.update(it.copy(status = newStatus))
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            try {
                orderRepository.delete(order)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

/**
 * 订单 UI 状态
 */
sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

/**
 * 衣物项数据类
 */
data class ClothesItem(
    val type: String,
    val price: Double,
    val damageRemark: String? = null
)

/**
 * 衣物类型和价格配置
 */
object ClothesTypePricing {
    val clothesTypes = listOf(
        ClothesType("毛衫", 20.0),
        ClothesType("羊绒大衣 (小)", 20.0),
        ClothesType("羊绒大衣 (中)", 25.0),
        ClothesType("羊绒大衣 (大)", 30.0),
        ClothesType("皮毛一体", 50.0),
        ClothesType("貂", 300.0),
        ClothesType("鞋", 15.0),
        ClothesType("裤子", 20.0)
    )
}

data class ClothesType(val name: String, val price: Double)
