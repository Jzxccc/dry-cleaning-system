package com.chaser.drycleaningsystem.ui.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaser.drycleaningsystem.data.entity.Clothes
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.data.repository.ClothesRepository
import com.chaser.drycleaningsystem.data.repository.CustomerRepository
import com.chaser.drycleaningsystem.data.repository.OrderRepository
import com.chaser.drycleaningsystem.utils.CameraHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单 ViewModel
 */
class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val clothesRepository: ClothesRepository,
    private val context: Context
) : ViewModel() {

    private val cameraHelper by lazy { CameraHelper(context) }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 搜索类型：order_no(订单号), customer(客户)
    private val _searchType = MutableStateFlow("order_no")
    val searchType: StateFlow<String> = _searchType

    // 搜索结果
    private val _searchResults = MutableStateFlow<List<Order>>(emptyList())
    val searchResults: StateFlow<List<Order>> = _searchResults

    val allOrders: Flow<List<Order>> = orderRepository.allOrders

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState

    private val _orderStatusFilter = MutableStateFlow<String?>(null)
    val orderStatusFilter: StateFlow<String?> = _orderStatusFilter

    // 当前选中的订单（用于详情页面）
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder

    init {
        observeOrders()
        observeSearch()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            allOrders.collect { orders ->
                // 根据筛选条件过滤订单
                val filteredOrders = orders.filter { order ->
                    val statusFilter = _orderStatusFilter.value
                    when (statusFilter) {
                        null -> true // 全部
                        "UNPAID" -> order.payType == "UNPAID" // 按支付类型筛选
                        else -> order.status == statusFilter // 按状态筛选
                    }
                }
                
                _uiState.value = OrderUiState.Success(filteredOrders)
                // 更新当前订单的状态
                _currentOrder.value?.let { currentOrder ->
                    val updatedOrder = orders.find { it.id == currentOrder.id }
                    if (updatedOrder != null && updatedOrder.status != currentOrder.status) {
                        _currentOrder.value = updatedOrder
                    }
                }
            }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery.collect { query ->
                if (query.isBlank()) {
                    _searchResults.value = emptyList()
                } else {
                    val results = when (searchType.value) {
                        "customer" -> orderRepository.searchOrdersByCustomer(query).first()
                        else -> orderRepository.searchOrdersByOrderNo(query).first()
                    }
                    _searchResults.value = results
                }
            }
        }
    }
    
    fun search(query: String) {
        _searchQuery.value = query
    }

    fun setSearchType(type: String) {
        _searchType.value = type
        // 重新触发搜索
        if (_searchQuery.value.isNotBlank()) {
            search(_searchQuery.value)
        }
    }

    fun filterByStatus(status: String?) {
        _orderStatusFilter.value = status
    }
    
    fun createOrder(
        customerId: Long,
        payType: String,
        urgent: Boolean,
        clothesList: List<ClothesItem>
    ): Long {
        var orderId = -1L
        
        runBlocking {
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
                    prepaid = 0.0,
                    payType = payType,
                    urgent = if (urgent) 1 else 0,
                    status = "UNWASHED",
                    expectedTime = null,
                    createTime = System.currentTimeMillis().toString()
                )

                orderId = orderRepository.insert(order)

                // 如果是储值支付，扣减客户余额
                if (payType == "PREPAID") {
                    val customer = customerRepository.getCustomerById(customerId)
                    customer?.let {
                        val newBalance = it.balance - totalPrice
                        customerRepository.update(it.copy(balance = newBalance))
                    }
                }

                // 添加衣物（使用 orderNo 而不是 orderId）
                clothesList.forEach { clothesItem ->
                    val clothes = Clothes(
                        orderId = orderNo,
                        type = clothesItem.type,
                        price = clothesItem.price,
                        damageRemark = null,
                        damageImage = null,
                        status = "UNWASHED",
                        createTime = System.currentTimeMillis().toString()
                    )
                    clothesRepository.insert(clothes)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
        
        return orderId
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

    // 设置当前选中的订单
    fun setCurrentOrder(order: Order) {
        _currentOrder.value = order
    }

    // 获取订单详情（包括衣物列表）
    fun getOrderDetail(orderId: Long): OrderDetailData? {
        return try {
            val order = runBlocking { orderRepository.getOrderById(orderId) }
            if (order != null) {
                // 设置当前订单
                _currentOrder.value = order
                // 获取衣物列表（使用订单号查询）
                val clothes = runBlocking { 
                    clothesRepository.getClothesByOrderId(order.orderNo).first() 
                }
                OrderDetailData(order, clothes)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // 获取衣物列表（作为 Flow）
    fun getClothesForOrder(orderNo: String): Flow<List<Clothes>> {
        return clothesRepository.getClothesByOrderId(orderNo)
    }

    // 更新订单状态
    fun updateOrderStatusSync(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val order = orderRepository.getOrderById(orderId)
                order?.let {
                    orderRepository.update(it.copy(status = newStatus))
                    
                    // 如果订单完成（已取），删除照片
                    if (newStatus == "FINISHED" && it.photoPath != null) {
                        cameraHelper.deleteOrderPhotos(orderId)
                        orderRepository.updatePhotoPath(orderId, null)
                    }
                    
                    // 更新当前订单状态
                    if (_currentOrder.value?.id == orderId) {
                        _currentOrder.value = _currentOrder.value?.copy(status = newStatus)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 删除订单
    fun deleteOrder(orderId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val order = orderRepository.getOrderById(orderId)
                if (order != null) {
                    // 如果订单是使用储值支付且未完成（非已取状态），退还金额
                    if (order.payType == "PREPAID" && order.status != "FINISHED") {
                        val customer = customerRepository.getCustomerById(order.customerId)
                        customer?.let {
                            // 退还订单金额到客户余额
                            val newBalance = it.balance + order.totalPrice
                            customerRepository.update(it.copy(balance = newBalance))
                        }
                    }

                    // 先删除衣物
                    clothesRepository.deleteByOrderId(order.orderNo)
                    // 删除照片
                    cameraHelper.deleteOrderPhotos(orderId)
                    // 再删除订单
                    orderRepository.delete(order)
                    onSuccess()
                } else {
                    onError("订单不存在")
                }
            } catch (e: Exception) {
                onError("删除失败：${e.message}")
            }
        }
    }

    /**
     * 刷新当前订单数据
     */
    fun refreshCurrentOrder(orderId: Long) {
        viewModelScope.launch {
            val order = orderRepository.getOrderById(orderId)
            if (order != null) {
                _currentOrder.value = order
            }
        }
    }

    /**
     * 保存照片路径到数据库
     */
    fun savePhotoPath(orderId: Long, photoPath: String) {
        viewModelScope.launch {
            try {
                orderRepository.updatePhotoPath(orderId, photoPath)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * 处理付款
     */
    fun processPayment(
        orderId: Long,
        customerId: Long,
        amount: Double,
        payType: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val order = orderRepository.getOrderById(orderId)
                if (order == null) {
                    onError("订单不存在")
                    return@launch
                }

                // 如果是储值支付，检查并扣减余额
                if (payType == "PREPAID") {
                    val customer = customerRepository.getCustomerById(customerId)
                    if (customer == null) {
                        onError("客户不存在")
                        return@launch
                    }

                    if (customer.balance < amount) {
                        onError("客户余额不足")
                        return@launch
                    }

                    // 扣减余额
                    val newBalance = customer.balance - amount
                    customerRepository.update(customer.copy(balance = newBalance))
                }

                // 更新订单支付类型
                orderRepository.update(order.copy(payType = payType))

                onSuccess()
            } catch (e: Exception) {
                onError("付款失败：${e.message}")
            }
        }
    }
}

/**
 * 订单详情数据
 */
data class OrderDetailData(
    val order: Order,
    val clothesList: List<Clothes>
)

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
        ClothesType("鞋", 15.0),
        ClothesType("裤子", 20.0),
        ClothesType("羽绒服", 20.0),
        ClothesType("羊绒大衣 (小)", 20.0),
        ClothesType("羊绒大衣 (中)", 25.0),
        ClothesType("羊绒大衣 (大)", 30.0),
        ClothesType("皮毛一体", 50.0),
        ClothesType("貂", 300.0)
    )
}

data class ClothesType(val name: String, val price: Double)
