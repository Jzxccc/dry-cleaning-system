package com.chaser.drycleaningsystem.data.dao

import androidx.room.*
import com.chaser.drycleaningsystem.data.entity.RechargeRecord
import kotlinx.coroutines.flow.Flow

/**
 * 充值记录数据访问对象
 */
@Dao
interface RechargeRecordDao {

    @Query("SELECT * FROM recharge_record ORDER BY create_time DESC")
    fun getAllRechargeRecords(): Flow<List<RechargeRecord>>

    @Query("SELECT * FROM recharge_record ORDER BY create_time DESC")
    suspend fun getAllRechargeRecordsList(): List<RechargeRecord>

    @Query("SELECT * FROM recharge_record WHERE customer_id = :customerId ORDER BY create_time DESC")
    fun getRechargeRecordsByCustomerId(customerId: Long): Flow<List<RechargeRecord>>

    @Query("SELECT * FROM recharge_record WHERE date(create_time/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getRechargeRecordsByDate(date: Long): List<RechargeRecord>

    @Query("SELECT * FROM recharge_record WHERE strftime('%Y-%m', create_time/1000, 'unixepoch') = strftime('%Y-%m', :timestamp/1000, 'unixepoch') ORDER BY create_time DESC")
    suspend fun getRechargeRecordsByMonth(timestamp: Long): List<RechargeRecord>

    @Query("SELECT SUM(recharge_amount) FROM recharge_record WHERE date(create_time/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getRechargeIncomeByDate(date: Long): Double?

    @Query("SELECT SUM(recharge_amount) FROM recharge_record WHERE strftime('%Y-%m', create_time/1000, 'unixepoch') = strftime('%Y-%m', :timestamp/1000, 'unixepoch')")
    suspend fun getRechargeIncomeByMonth(timestamp: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RechargeRecord): Long

    @Update
    suspend fun update(record: RechargeRecord)

    @Delete
    suspend fun delete(record: RechargeRecord)
}
