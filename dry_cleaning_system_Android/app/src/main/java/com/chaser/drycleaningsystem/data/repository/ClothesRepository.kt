package com.chaser.drycleaningsystem.data.repository

import com.chaser.drycleaningsystem.data.dao.ClothesDao
import com.chaser.drycleaningsystem.data.entity.Clothes
import kotlinx.coroutines.flow.Flow

/**
 * 衣物数据仓库
 */
class ClothesRepository(private val clothesDao: ClothesDao) {
    
    fun getClothesByOrderId(orderId: String): Flow<List<Clothes>> {
        return clothesDao.getClothesByOrderId(orderId)
    }
    
    suspend fun insert(clothes: Clothes): Long {
        return clothesDao.insert(clothes)
    }
    
    suspend fun update(clothes: Clothes) {
        clothesDao.update(clothes)
    }
    
    suspend fun delete(clothes: Clothes) {
        clothesDao.delete(clothes)
    }
    
    suspend fun deleteById(clothesId: Long) {
        clothesDao.deleteById(clothesId)
    }
    
    suspend fun deleteByOrderId(orderId: String) {
        clothesDao.deleteByOrderId(orderId)
    }
}
