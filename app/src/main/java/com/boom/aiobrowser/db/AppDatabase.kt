package com.boom.aiobrowser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.boom.aiobrowser.data.model.DownloadModel
import com.boom.aiobrowser.tools.AppLogs


@Database(entities = [DownloadModel::class], version = 2, exportSchema = false)
public abstract class AppDatabase : RoomDatabase() {


    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "password_db")
                    .allowMainThreadQueries()
                    .addMigrations(ZMigration)//增加数据库迁移
                    .build()
                    .also { Instance = it }
            }
        }
    }
    /**
     * 数据库升级
     */
    private object ZMigration : Migration(1, 2) {
        var TAG = "AppDatabase"

        override fun migrate(database: SupportSQLiteDatabase) {
            AppLogs.dLog(TAG, "执行数据库升级: ")
            //loginUser表中增加字段gender
            database.execSQL("ALTER TABLE download_tab ADD downloadFileName TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE download_tab ADD downloadFilePath TEXT NOT NULL DEFAULT ''")
            //新建汽车数据表
            //database.exceSQL("CREATE TABLE Car (id INTEGER, name TEXT, PRIMARY KEY(id))")
        }
    }

}