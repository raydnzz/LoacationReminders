package com.udacity.project4.data.repository.location

import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.datasource.location.LocationDataSource
import com.udacity.project4.data.repository.dto.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocationRepository {

    companion object {
        private const val ERROR_MESSAGE = "Location not found"
    }

    override suspend fun add(location: Location) = withContext(dispatcher) {
        locationDataSource.create(location = location)
    }

    override suspend fun getLocations(): Result<List<Location>> = withContext(dispatcher) {
        try {
            val locations = locationDataSource.getLocations()
            if (locations.isEmpty()) {
                return@withContext Result.Error("Locations empty")
            }
            return@withContext Result.Success(locations)
        } catch (e: Exception) {
            return@withContext Result.Error(e.localizedMessage)
        }
    }

    override suspend fun getLocation(id: String): Result<Location> = withContext(dispatcher) {
        return@withContext try {
            locationDataSource.getLocation(id = id).let {
                if (it == null) {
                    Result.Error(ERROR_MESSAGE)
                } else {
                    Result.Success(it)
                }
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun delete(location: Location) = withContext(dispatcher) {
        locationDataSource.delete(location = location)
    }
}