package com.udacity.project4.di.modules

import com.udacity.project4.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single { AppDatabase.init(androidContext()) }
}

val daoModule = module {
    includes(dbModule)

    single { get<AppDatabase>().getLocationDao() }
}