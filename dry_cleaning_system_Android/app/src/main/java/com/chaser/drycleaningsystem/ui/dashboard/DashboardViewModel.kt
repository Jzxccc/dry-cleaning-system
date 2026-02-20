package com.chaser.drycleaningsystem.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaser.drycleaningsystem.data.repository.CustomerRepository
import com.chaser.drycleaningsystem.data.repository.OrderRepository
import com.chaser.drycleaningsystem.data.repository.RechargeRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 仪表板 UI 状态
 */
data class DashboardUiState(
    val todayOrders: Int = 0,
    val pendingOrders: Int = 0,
    val monthCustomers: Int = 0,
    val monthRevenue: Double = 0.0,
    val isLoading: Boolean = false
)

/**
 * 仪表板 ViewModel
 */
class DashboardViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val rechargeRecordRepository: RechargeRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * 加载统计数据
     */
    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // 获取所有数据
                val allOrders = orderRepository.getAllOrders()
                val allCustomers = customerRepository.getAllCustomers()
                val allRechargeRecords = rechargeRecordRepository.getAllRecords()
                
                // 计算今日订单
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)
                val todayStartMillis = today.timeInMillis
                
                val todayOrders = allOrders.count { order ->
                    try {
                        order.createTime.toLongOrNull() ?: 0L >= todayStartMillis
                    } catch (e: Exception) {
                        false
                    }
                }
                
                // 计算待处理订单（未洗状态）
                val pendingOrders = allOrders.count { order ->
                    order.status == "UNWASHED"
                }
                
                // 计算本月客户 - 统计所有客户
                val monthCustomers = allCustomers.size
                
                // 计算月营收（现金收入 + 充值收入）
                // 现金收入：只统计现金支付的订单
                // 充值收入：所有充值记录的金额
                val cashRevenue = allOrders
                    .filter { order -> order.payType == "CASH" }
                    .sumOf { it.totalPrice.toDouble() }
                
                val rechargeRevenue = allRechargeRecords.sumOf { it.rechargeAmount }
                
                val monthRevenue = cashRevenue + rechargeRevenue

                _uiState.value = DashboardUiState(
                    todayOrders = todayOrders,
                    pendingOrders = pendingOrders,
                    monthCustomers = monthCustomers,
                    monthRevenue = monthRevenue,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * 刷新数据（从外部调用，如页面返回时）
     */
    fun refresh() {
        loadStatistics()
    }
}
