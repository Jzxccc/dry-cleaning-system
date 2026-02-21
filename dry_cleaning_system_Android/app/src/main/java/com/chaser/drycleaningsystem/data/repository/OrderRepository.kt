package com.chaser.drycleaningsystem.data.repository

import com.chaser.drycleaningsystem.data.dao.OrderDao
import com.chaser.drycleaningsystem.data.entity.Order
import kotlinx.coroutines.flow.Flow

/**
 * 订单数据仓库
 */
class OrderRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    /**
     * 获取所有订单（非 Flow 版本）
     */
    suspend fun getAllOrders(): List<Order> {
        return orderDao.getAllOrdersList()
    }

    fun getOrdersByCustomerId(customerId: Long): Flow<List<Order>> {
        return orderDao.getOrdersByCustomerId(customerId)
    }
    
    fun searchOrdersByOrderNo(keyword: String): Flow<List<Order>> {
        return orderDao.searchOrdersByOrderNo(keyword)
    }

    fun searchOrdersByCustomer(keyword: String): Flow<List<Order>> {
        return orderDao.searchOrdersByCustomer(keyword)
    }

    fun getOrdersByStatus(status: String): Flow<List<Order>> {
        return orderDao.getOrdersByStatus(status)
    }
    
    suspend fun getOrderById(orderId: Long): Order? {
        return orderDao.getOrderById(orderId)
    }
    
    suspend fun insert(order: Order): Long {
        return orderDao.insert(order)
    }
    
    suspend fun update(order: Order) {
        orderDao.update(order)
    }
    
    suspend fun delete(order: Order) {
        orderDao.delete(order)
    }
    
    suspend fun deleteById(orderId: Long) {
        orderDao.deleteById(orderId)
    }

    /**
     * 更新订单照片路径
     */
    suspend fun updatePhotoPath(orderId: Long, photoPath: String?) {
        orderDao.updatePhotoPath(orderId, photoPath)
    }

    /**
     * 删除订单（带照片清理）
     */
    suspend fun deleteOrderWithPhotos(orderId: Long, context: android.content.Context) {
        // 先获取订单信息
        val order = getOrderById(orderId)
        
        // 删除照片
        if (order?.photoPath != null) {
            val cameraHelper = com.chaser.drycleaningsystem.utils.CameraHelper(context)
            cameraHelper.deleteOrderPhotos(orderId)
        }
        
        // 删除订单记录
        deleteById(orderId)
    }

    // 统计相关方法
    suspend fun getOrdersByDate(timestamp: Long): List<Order> {
        return orderDao.getOrdersByDate(timestamp)
    }

    suspend fun getOrdersByMonth(timestamp: Long): List<Order> {
        return orderDao.getOrdersByMonth(timestamp)
    }

    suspend fun getCashIncomeByDate(timestamp: Long): Double? {
        return orderDao.getCashIncomeByDate(timestamp)
    }

    suspend fun getCashIncomeByMonth(timestamp: Long): Double? {
        return orderDao.getCashIncomeByMonth(timestamp)
    }
}
