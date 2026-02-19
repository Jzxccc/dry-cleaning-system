package com.chaser.drycleaningsystem.data

import android.content.Context
import com.chaser.drycleaningsystem.data.repository.*

/**
 * 数据注入提供者
 */
object DataInjection {
    
    private var database: AppDatabase? = null
    private var customerRepository: CustomerRepository? = null
    private var orderRepository: OrderRepository? = null
    private var clothesRepository: ClothesRepository? = null
    private var rechargeRecordRepository: RechargeRecordRepository? = null
    
    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = AppDatabase.getDatabase(context)
            database = instance
            instance
        }
    }
    
    fun getCustomerRepository(context: Context): CustomerRepository {
        return customerRepository ?: synchronized(this) {
            val database = getDatabase(context)
            val instance = CustomerRepository(database.customerDao())
            customerRepository = instance
            instance
        }
    }
    
    fun getOrderRepository(context: Context): OrderRepository {
        return orderRepository ?: synchronized(this) {
            val database = getDatabase(context)
            val instance = OrderRepository(database.orderDao())
            orderRepository = instance
            instance
        }
    }
    
    fun getClothesRepository(context: Context): ClothesRepository {
        return clothesRepository ?: synchronized(this) {
            val database = getDatabase(context)
            val instance = ClothesRepository(database.clothesDao())
            clothesRepository = instance
            instance
        }
    }
    
    fun getRechargeRecordRepository(context: Context): RechargeRecordRepository {
        return rechargeRecordRepository ?: synchronized(this) {
            val database = getDatabase(context)
            val instance = RechargeRecordRepository(database.rechargeRecordDao())
            rechargeRecordRepository = instance
            instance
        }
    }
}
