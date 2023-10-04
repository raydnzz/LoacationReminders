package com.udacity.project4.di.modules

import com.udacity.project4.data.datasource.location.LocationDataSource
import com.udacity.project4.data.datasource.location.LocationDataSourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appDataSource = module {
    includes(daoModule)

    singleOf(::LocationDataSourceImpl) { bind<LocationDataSource>() }
}