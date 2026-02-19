package com.chaser.drycleaningsystem.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaser.drycleaningsystem.data.entity.Order
import com.chaser.drycleaningsystem.data.entity.RechargeRecord
import com.chaser.drycleaningsystem.data.repository.OrderRepository
import com.chaser.drycleaningsystem.data.repository.RechargeRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 统计报表 ViewModel
 */
class StatisticsViewModel(
    private val orderRepository: OrderRepository,
    private val rechargeRecordRepository: RechargeRecordRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Long>(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate

    private val _reportType = MutableStateFlow<ReportType>(ReportType.DAILY)
    val reportType: StateFlow<ReportType> = _reportType

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState

    init {
        loadStatistics()
    }

    fun setReportType(type: ReportType) {
        _reportType.value = type
        loadStatistics()
    }

    fun setSelectedDate(timestamp: Long) {
        _selectedDate.value = timestamp
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            try {
                _uiState.value = StatisticsUiState.Loading

                val timestamp = _selectedDate.value
                val type = _reportType.value

                val cashIncome: Double
                val rechargeIncome: Double
                val orders: List<Order>

                when (type) {
                    ReportType.DAILY -> {
                        cashIncome = orderRepository.getCashIncomeByDate(timestamp) ?: 0.0
                        rechargeIncome = rechargeRecordRepository.getRechargeIncomeByDate(timestamp) ?: 0.0
                        orders = orderRepository.getOrdersByDate(timestamp)
                    }
                    ReportType.MONTHLY -> {
                        cashIncome = orderRepository.getCashIncomeByMonth(timestamp) ?: 0.0
                        rechargeIncome = rechargeRecordRepository.getRechargeIncomeByMonth(timestamp) ?: 0.0
                        orders = orderRepository.getOrdersByMonth(timestamp)
                    }
                }

                val totalIncome = cashIncome + rechargeIncome
                val pendingOrders = orders.count { it.status == "待取件" }

                _uiState.value = StatisticsUiState.Success(
                    cashIncome = cashIncome,
                    rechargeIncome = rechargeIncome,
                    totalIncome = totalIncome,
                    pendingOrders = pendingOrders,
                    orders = orders,
                    dateStr = formatDate(timestamp, type)
                )
            } catch (e: Exception) {
                _uiState.value = StatisticsUiState.Error("加载失败：${e.message}")
            }
        }
    }

    private fun formatDate(timestamp: Long, type: ReportType): String {
        val pattern = if (type == ReportType.DAILY) "yyyy 年 MM 月 dd 日" else "yyyy 年 MM 月"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

/**
 * 报表类型
 */
enum class ReportType {
    DAILY,
    MONTHLY
}

/**
 * 统计 UI 状态
 */
sealed class StatisticsUiState {
    object Loading : StatisticsUiState()
    data class Success(
        val cashIncome: Double,
        val rechargeIncome: Double,
        val totalIncome: Double,
        val pendingOrders: Int,
        val orders: List<Order>,
        val dateStr: String
    ) : StatisticsUiState()
    data class Error(val message: String) : StatisticsUiState()
}
