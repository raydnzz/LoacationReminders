package com.udacity.project4.data.repository.location

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.udacity.project4.data.database.AppDatabase
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.datasource.location.LocationDataSourceImpl
import com.udacity.project4.data.repository.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
@ExperimentalCoroutinesApi
@SmallTest
class LocationRepositoryImplTest {
    private companion object {
        val context: Context = ApplicationProvider.getApplicationContext()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val locationDao = db.getLocationDao()
        val locationRepository = LocationRepositoryImpl(LocationDataSourceImpl(locationDao))
        val location = Location(
            id = "15",
            title = "Test",
            description = "description",
            locationName = "locationName",
            longitude = 3.2,
            latitude = 3.4
        )

        @JvmStatic
        @AfterClass
        fun teaDown() {
            db.close()
        }
    }

    class InsertTest {
        @After
        fun teaDown() = runTest {
            locationDao.delete(location)
        }

        @Test
        fun test() = runTest {
            locationRepository.add(location)

            val actual = locationDao.getLocation(location.id)

            MatcherAssert.assertThat(actual, CoreMatchers.notNullValue())

        }
    }

    class GetAllTestWithResultSuccess {

        @Before
        fun setUp() = runTest {
            locationDao.insert(location)
            locationDao.insert(location.copy(id = "16"))
            locationDao.insert(location.copy(id = "17"))
        }

        @After
        fun teaDown() = runTest {
            locationDao.delete(location)
            locationDao.delete(location.copy(id = "16"))
            locationDao.delete(location.copy(id = "17"))
        }

        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {
            val result = locationRepository.getLocations() as Result.Success
            val actual = result.data

            MatcherAssert.assertThat(actual, CoreMatchers.notNullValue())
        }
    }

    class GetAllTestWithResultError {
        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {
            val result = locationRepository.getLocations() as Result.Error
            val actual = result.message

            MatcherAssert.assertThat(actual, `is`("Locations empty"))
        }
    }

    class GetLocationTestResultSuccess {
        @Before
        fun setUp() = runTest {
            locationDao.insert(location)
        }

        @After
        fun teaDown() = runTest {
            locationDao.delete(location)
        }

        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {

            val result = locationRepository.getLocation(location.id) as Result.Success
            val actual = result.data

            MatcherAssert.assertThat(actual.id, `is`(location.id))
            MatcherAssert.assertThat(actual.title, `is`(location.title))
            MatcherAssert.assertThat(actual.description, `is`(location.description))
            MatcherAssert.assertThat(actual.locationName, `is`(location.locationName))
            MatcherAssert.assertThat(actual.longitude, `is`(location.longitude))
            MatcherAssert.assertThat(actual.latitude, `is`(location.latitude))
        }
    }

    class GetLocationTWithResultError {
        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {
            val result = locationRepository.getLocation(location.id) as Result.Error
            val actual = result.message

            MatcherAssert.assertThat(actual, `is`("Location not found"))
        }
    }

    class DeleteTest {
        @Before
        fun setUp() = runTest {
            locationDao.insert(location)
        }

        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {
            locationRepository.delete(location)

            val actual = locationDao.getLocation(location.id)

            MatcherAssert.assertThat(actual, CoreMatchers.nullValue())
        }
    }
}