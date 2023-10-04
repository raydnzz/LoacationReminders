package com.udacity.project4.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.ui.dto.LocationSelectDto

class MapViewModel : ViewModel() {

    var locationSelectDto: LocationSelectDto? = null
        private set
    private var marker: Marker? = null
    private var pointOfInterest: PointOfInterest? = null

    fun updateMarkerAndPointOfInterest(marker: Marker, pointOfInterest: PointOfInterest) {
        removeMarker()
        marker.showInfoWindow()
        this.pointOfInterest = pointOfInterest
        this.marker = marker
        updateLocationDto()
    }

    private fun updateLocationDto() {
        pointOfInterest?.let {
            locationSelectDto = LocationSelectDto(
                name = it.name,
                longitude = it.latLng.longitude,
                latitude = it.latLng.latitude
            )
        }
    }

    private fun removeMarker() {
        marker?.remove()
        marker = null
    }

}