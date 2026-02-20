package com.chaser.drycleaningsystem.data.repository

import com.chaser.drycleaningsystem.data.dao.CustomerDao
import com.chaser.drycleaningsystem.data.entity.Customer
import kotlinx.coroutines.flow.Flow

/**
 * 客户数据仓库
 */
class CustomerRepository(private val customerDao: CustomerDao) {

    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()

    /**
     * 获取所有客户（非 Flow 版本）
     */
    suspend fun getAllCustomers(): List<Customer> {
        return customerDao.getAllCustomersList()
    }

    fun searchCustomers(keyword: String): Flow<List<Customer>> {
        return customerDao.searchCustomers(keyword)
    }
    
    suspend fun getCustomerById(customerId: Long): Customer? {
        return customerDao.getCustomerById(customerId)
    }
    
    suspend fun getCustomerByPhone(phone: String): Customer? {
        return customerDao.getCustomerByPhone(phone)
    }
    
    suspend fun insert(customer: Customer): Long {
        return customerDao.insert(customer)
    }
    
    suspend fun update(customer: Customer) {
        customerDao.update(customer)
    }
    
    suspend fun delete(customer: Customer) {
        customerDao.delete(customer)
    }
    
    suspend fun deleteById(customerId: Long) {
        customerDao.deleteById(customerId)
    }
}
