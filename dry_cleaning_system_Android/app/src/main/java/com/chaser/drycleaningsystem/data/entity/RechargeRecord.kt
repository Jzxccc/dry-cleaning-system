package com.chaser.drycleaningsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 充值记录实体类
 */
@Entity(tableName = "recharge_record")
data class RechargeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "customer_id") val customerId: Long,
    @ColumnInfo(name = "recharge_amount") val rechargeAmount: Double,
    @ColumnInfo(name = "gift_amount") val giftAmount: Double,
    @ColumnInfo(name = "create_time") val createTime: String = System.currentTimeMillis().toString()
)
