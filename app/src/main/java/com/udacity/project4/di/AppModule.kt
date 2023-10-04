package com.udacity.project4.di

import android.Manifest
import android.os.Build
import com.udacity.project4.di.modules.useCaseModule
import com.udacity.project4.ui.locationdetail.LocationDetailViewModel
import com.udacity.project4.ui.locations.LocationListViewModel
import com.udacity.project4.utils.Constants.PERMISSION_NAME
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val locationListModule = module {
    includes(useCaseModule)
    viewModelOf(::LocationListViewModel)
}

val locationDetailModule = module {
    includes(useCaseModule)
    viewModelOf(::LocationDetailViewModel)
}

val permissionModule = module {
    fun handlePermission(): Array<String> {
        var permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }

        return permissions
    }
    single(named(PERMISSION_NAME)) { handlePermission() }
}


val appModule = listOf(
    locationListModule, locationDetailModule, permissionModule
)
