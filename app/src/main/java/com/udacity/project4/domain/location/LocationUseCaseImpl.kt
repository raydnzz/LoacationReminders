package com.udacity.project4.domain.location

import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.repository.dto.Result
import com.udacity.project4.data.repository.location.RemindersLocalRepository

class LocationUseCaseImpl(private val remindersLocalRepository: RemindersLocalRepository) : LocationUseCase {
    override suspend fun create(location: Location) = remindersLocalRepository.add(location)

    override suspend fun getLocations(): Result<List<Location>> = remindersLocalRepository.getLocations()
    override suspend fun getLocation(id: String): Result<Location> =
        remindersLocalRepository.getLocation(id)

    override suspend fun delete(location: Location) {
        remindersLocalRepository.delete(location)
    }
}