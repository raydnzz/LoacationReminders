package com.udacity.project4.di.modules

import com.udacity.project4.domain.location.LocationUseCase
import com.udacity.project4.domain.location.LocationUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    includes(appRepository)

    factoryOf(::LocationUseCaseImpl) { bind<LocationUseCase>() }
}