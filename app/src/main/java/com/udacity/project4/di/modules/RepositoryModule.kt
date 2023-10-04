package com.udacity.project4.di.modules

import com.udacity.project4.data.repository.location.LocationRepository
import com.udacity.project4.data.repository.location.LocationRepositoryImpl
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dispatcherModule = module {
    single {
        Dispatchers.IO
    }
}
val appRepository = module {
    includes(appDataSource, dispatcherModule)

    singleOf(::LocationRepositoryImpl) {
        bind<LocationRepository>()
    }
}