package com.udacity.project4.data.datasource.location

import com.udacity.project4.data.database.daos.LocationDao
import com.udacity.project4.data.database.entites.Location

class LocationDataSourceImpl(
    private val locationDao: LocationDao
) : LocationDataSource {

    override suspend fun create(location: Location) = locationDao.insert(location)

    override suspend fun getLocations(): List<Location> = locationDao.getAll()

    override suspend fun getLocation(id: String): Location? = locationDao.getLocation(id)

    override suspend fun delete(location: Location) {
        locationDao.delete(location)
    }
}