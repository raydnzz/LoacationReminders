package com.udacity.project4.ui.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.repository.dto.Result
import com.udacity.project4.domain.location.LocationUseCase
import com.udacity.project4.utils.SingleLiveEvent
import com.udacity.project4.utils.isSuccess
import kotlinx.coroutines.launch

class LocationListViewModel(
    private val locationUseCase: LocationUseCase
) : ViewModel() {

    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> = _locations
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()


    fun getLocation() {
        showLoading.value = true
        viewModelScope.launch {
            val result = locationUseCase.getLocations()

            result.isSuccess {
                _locations.value = it
                showToast.value = "Get location success"
            }

            if (result is Result.Error) {
                result.message?.let {
                    showSnackBar.value = it
                }
            }

            showLoading.value = false
        }
    }
}