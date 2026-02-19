package com.chaser.drycleaningsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 衣物实体类
 */
@Entity(tableName = "clothes")
data class Clothes(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "order_id") val orderId: String,
    val type: String,
    val price: Double,
    @ColumnInfo(name = "damage_remark") val damageRemark: String?,
    @ColumnInfo(name = "damage_image") val damageImage: String?,
    val status: String,
    @ColumnInfo(name = "create_time") val createTime: String = System.currentTimeMillis().toString()
)
