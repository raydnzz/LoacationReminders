package com.udacity.project4.ui.locationdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.domain.location.LocationUseCase
import com.udacity.project4.ui.dto.LocationSelectDto
import com.udacity.project4.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationDetailViewModel(
    private val locationUseCase: LocationUseCase
) : ViewModel() {

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val locationDto = MutableLiveData<LocationSelectDto>()
    private val _location: MutableStateFlow<Location?> = MutableStateFlow(null)
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun saveLocation(callBack: (geofence: Geofence) -> Unit) {
        viewModelScope.launch {
            updateLocation()
            _location.value?.let {
                locationUseCase.create(it)
                it.copy(id = it.id).let { result ->
                    _location.value = result
                    showLoading.value = false
                    showToast.value = "Save location success"
                    callBack.invoke(createGeofence(it))
                }
            }
        }
    }

    fun deleteLocation() {
        viewModelScope.launch {
            _location.value?.let {
                locationUseCase.delete(it)
            }
        }
    }

    fun setLocation(location: Location) {
        _location.value = location
    }

    private fun createGeofence(location: Location) = location.run {
        Geofence.Builder().setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).setRequestId(id.toString())
            .setCircularRegion(latitude, longitude, 150f).build()
    }

    private fun updateLocation() {
        _location.update {
            it?.copy(
                title = title.value.orEmpty(),
                description = description.value.orEmpty(),
                locationName = locationDto.value?.name ?: "",
                longitude = locationDto.value?.longitude ?: 0.0,
                latitude = locationDto.value?.latitude ?: 0.0
            ) ?: Location(
                title = title.value.orEmpty(),
                description = description.value.orEmpty(),
                locationName = locationDto.value?.name ?: "",
                longitude = locationDto.value?.longitude ?: 0.0,
                latitude = locationDto.value?.latitude ?: 0.0
            )
        }
    }
}