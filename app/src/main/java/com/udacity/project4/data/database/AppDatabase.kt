package com.udacity.project4.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.project4.data.database.AppDatabase.Companion.DB_VERSION
import com.udacity.project4.data.database.daos.LocationDao
import com.udacity.project4.data.database.entites.Location

@Database(entities = [Location::class], version = DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLocationDao(): LocationDao

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "AppDatabase"

        fun init(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .build()
    }
}