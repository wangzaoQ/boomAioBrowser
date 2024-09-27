package com.boom.aiobrowser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.boom.aiobrowser.data.model.DownloadModel


@Database(entities = [DownloadModel::class], version = 1, exportSchema = false)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "password_db")
                    .allowMainThreadQueries().build()
                    .also { Instance = it }
            }
        }
    }
}