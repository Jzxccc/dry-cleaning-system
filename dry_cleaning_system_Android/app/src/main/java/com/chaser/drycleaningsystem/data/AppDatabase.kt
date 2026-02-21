package com.chaser.drycleaningsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
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
    version = 3,
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
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        // 版本 2 -> 3: 添加 orders 表的 photo_path 字段
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE orders ADD COLUMN photo_path TEXT")
            }
        }
    }
}
