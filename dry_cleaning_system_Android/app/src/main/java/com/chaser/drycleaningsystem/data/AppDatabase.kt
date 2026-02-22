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
    version = 4,
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
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
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

        // 版本 3 -> 4: 确保 customer 表 balance 字段正确映射
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // balance 字段已存在，无需修改，此迁移用于触发版本升级
            }
        }
    }
}
