package com.chaser.drycleaningsystem.data.repository

import com.chaser.drycleaningsystem.data.dao.RechargeRecordDao
import com.chaser.drycleaningsystem.data.entity.RechargeRecord
import kotlinx.coroutines.flow.Flow

/**
 * 充值记录数据仓库
 */
class RechargeRecordRepository(private val rechargeRecordDao: RechargeRecordDao) {
    
    fun getAllRechargeRecords(): Flow<List<RechargeRecord>> {
        return rechargeRecordDao.getAllRechargeRecords()
    }
    
    fun getRechargeRecordsByCustomerId(customerId: Long): Flow<List<RechargeRecord>> {
        return rechargeRecordDao.getRechargeRecordsByCustomerId(customerId)
    }
    
    suspend fun insert(record: RechargeRecord): Long {
        return rechargeRecordDao.insert(record)
    }
    
    suspend fun update(record: RechargeRecord) {
        rechargeRecordDao.update(record)
    }
    
    suspend fun delete(record: RechargeRecord) {
        rechargeRecordDao.delete(record)
    }

    // 统计相关方法
    suspend fun getRechargeIncomeByDate(date: Long): Double? {
        return rechargeRecordDao.getRechargeIncomeByDate(date)
    }

    suspend fun getRechargeIncomeByMonth(timestamp: Long): Double? {
        return rechargeRecordDao.getRechargeIncomeByMonth(timestamp)
    }
}
