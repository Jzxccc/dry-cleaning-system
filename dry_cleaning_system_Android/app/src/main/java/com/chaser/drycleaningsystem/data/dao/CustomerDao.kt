package com.chaser.drycleaningsystem.data.dao

import androidx.room.*
import com.chaser.drycleaningsystem.data.entity.Customer
import kotlinx.coroutines.flow.Flow

/**
 * 客户数据访问对象
 */
@Dao
interface CustomerDao {

    @Query("SELECT * FROM customer ORDER BY create_time DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customer ORDER BY create_time DESC")
    suspend fun getAllCustomersList(): List<Customer>

    @Query("SELECT * FROM customer WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Long): Customer?

    @Query("SELECT * FROM customer WHERE phone = :phone")
    suspend fun getCustomerByPhone(phone: String): Customer?

    @Query("SELECT * FROM customer WHERE name = :name")
    suspend fun getCustomerByName(name: String): Customer?

    @Query("SELECT * FROM customer WHERE phone LIKE '%' || :keyword || '%' OR name LIKE '%' || :keyword || '%'")
    fun searchCustomers(keyword: String): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer)

    @Delete
    suspend fun delete(customer: Customer)

    @Query("DELETE FROM customer WHERE id = :customerId")
    suspend fun deleteById(customerId: Long)
}
