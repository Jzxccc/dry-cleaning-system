package com.chaser.drycleaningsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 订单实体类
 */
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "order_no") val orderNo: String,
    @ColumnInfo(name = "customer_id") val customerId: Long,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
    val prepaid: Double = 0.0,
    @ColumnInfo(name = "pay_type") val payType: String,
    val urgent: Int = 0,
    val status: String,
    @ColumnInfo(name = "expected_time") val expectedTime: String?,
    @ColumnInfo(name = "create_time") val createTime: String = System.currentTimeMillis().toString()
)
