package com.udacity.project4.data.repository.location

import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.repository.dto.Result

interface LocationRepository {
    suspend fun add(location: Location)

    suspend fun getLocations(): Result<List<Location>>
    suspend fun getLocation(id: String): Result<Location>

    suspend fun delete(location: Location)
}