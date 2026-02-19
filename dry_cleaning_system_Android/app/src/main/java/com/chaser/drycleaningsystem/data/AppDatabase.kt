package com.chaser.drycleaningsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chaser.drycleaningsystem.data.dao.*
import com.chaser.drycleaningsystem.data.entity.*

/**
 * App 数据库
 */
@Database(
    entities = [
        Customer::class,
        Order::class,
        Clothes::class,
        RechargeRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun customerDao(): CustomerDao
    abstract fun orderDao(): OrderDao
    abstract fun clothesDao(): ClothesDao
    abstract fun rechargeRecordDao(): RechargeRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dry_cleaning_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
