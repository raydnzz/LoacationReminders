package com.udacity.project4.data.datasource.location

import com.udacity.project4.data.database.entites.Location

interface LocationDataSource {

    suspend fun create(location: Location)

    suspend fun getLocations(): List<Location>

    suspend fun getLocation(id: String): Location?

    suspend fun delete(location: Location)
}