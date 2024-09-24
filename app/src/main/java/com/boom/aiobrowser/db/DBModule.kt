package com.boom.aiobrowser.db

import androidx.annotation.NonNull
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.boom.aiobrowser.APP


private const val DB_MODULE_TAG = "DBModule"

val dbModule =  {
    Room.databaseBuilder(APP.instance, AppDatabase::class.java, "app")
//            .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
//        .addMigrations(MIGRATION_26_27, MIGRATION_27_28)
        .build()
}

