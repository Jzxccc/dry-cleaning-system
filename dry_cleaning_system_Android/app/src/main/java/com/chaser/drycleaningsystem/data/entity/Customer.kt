package com.chaser.drycleaningsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 客户实体类
 */
@Entity(tableName = "customer")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "wechat") val wechat: String?,
    @ColumnInfo(name = "balance") val balance: Double = 0.0,
    @ColumnInfo(name = "create_time") val createTime: String = System.currentTimeMillis().toString(),
    @ColumnInfo(name = "note") val note: String? = null
)
