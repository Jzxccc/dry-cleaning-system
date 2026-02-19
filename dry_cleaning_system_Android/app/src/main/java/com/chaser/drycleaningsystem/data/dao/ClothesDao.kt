package com.chaser.drycleaningsystem.data.dao

import androidx.room.*
import com.chaser.drycleaningsystem.data.entity.Clothes
import kotlinx.coroutines.flow.Flow

/**
 * 衣物数据访问对象
 */
@Dao
interface ClothesDao {

    @Query("SELECT * FROM clothes WHERE order_id = :orderId ORDER BY create_time")
    fun getClothesByOrderId(orderId: String): Flow<List<Clothes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clothes: Clothes): Long

    @Update
    suspend fun update(clothes: Clothes)

    @Delete
    suspend fun delete(clothes: Clothes)

    @Query("DELETE FROM clothes WHERE id = :clothesId")
    suspend fun deleteById(clothesId: Long)

    @Query("DELETE FROM clothes WHERE order_id = :orderId")
    suspend fun deleteByOrderId(orderId: String)
}
