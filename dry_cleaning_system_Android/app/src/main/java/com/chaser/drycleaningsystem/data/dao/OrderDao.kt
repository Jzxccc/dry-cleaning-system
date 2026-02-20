package com.chaser.drycleaningsystem.data.dao

import androidx.room.*
import com.chaser.drycleaningsystem.data.entity.Order
import kotlinx.coroutines.flow.Flow

/**
 * 订单数据访问对象
 */
@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY create_time DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): Order?

    @Query("SELECT * FROM orders WHERE customer_id = :customerId ORDER BY create_time DESC")
    fun getOrdersByCustomerId(customerId: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE order_no = :orderNo")
    suspend fun getOrderByOrderNo(orderNo: String): Order?

    @Query("SELECT * FROM orders WHERE order_no LIKE '%' || :keyword || '%' ORDER BY create_time DESC")
    fun searchOrdersByOrderNo(keyword: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE customer_id IN (SELECT id FROM customer WHERE name LIKE '%' || :keyword || '%' OR phone LIKE '%' || :keyword || '%') ORDER BY create_time DESC")
    fun searchOrdersByCustomer(keyword: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY create_time DESC")
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE date(create_time/1000, 'unixepoch') = date(:timestamp/1000, 'unixepoch') ORDER BY create_time DESC")
    suspend fun getOrdersByDate(timestamp: Long): List<Order>

    @Query("SELECT * FROM orders WHERE strftime('%Y-%m', create_time/1000, 'unixepoch') = strftime('%Y-%m', :timestamp/1000, 'unixepoch') ORDER BY create_time DESC")
    suspend fun getOrdersByMonth(timestamp: Long): List<Order>

    @Query("SELECT SUM(total_price) FROM orders WHERE date(create_time/1000, 'unixepoch') = date(:timestamp/1000, 'unixepoch') AND pay_type = '现金'")
    suspend fun getCashIncomeByDate(timestamp: Long): Double?

    @Query("SELECT SUM(total_price) FROM orders WHERE strftime('%Y-%m', create_time/1000, 'unixepoch') = strftime('%Y-%m', :timestamp/1000, 'unixepoch') AND pay_type = '现金'")
    suspend fun getCashIncomeByMonth(timestamp: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order): Long

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteById(orderId: Long)
}
