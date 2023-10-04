package com.udacity.project4.domain.location

import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.repository.dto.Result
import com.udacity.project4.data.repository.location.LocationRepository

class LocationUseCaseImpl(private val locationRepository: LocationRepository) : LocationUseCase {
    override suspend fun create(location: Location) = locationRepository.add(location)

    override suspend fun getLocations(): Result<List<Location>> = locationRepository.getLocations()
    override suspend fun getLocation(id: String): Result<Location> =
        locationRepository.getLocation(id)

    override suspend fun delete(location: Location) {
        locationRepository.delete(location)
    }
}