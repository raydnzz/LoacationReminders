package com.udacity.project4.data.datasource.location

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.udacity.project4.data.database.AppDatabase
import com.udacity.project4.data.database.entites.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
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
class LocationDataSourceImplTest {
    private companion object {
        val context: Context = ApplicationProvider.getApplicationContext()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val locationDao = db.getLocationDao()
        val locationDataSource = LocationDataSourceImpl(locationDao)
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
            locationDataSource.create(location)

            val actual = locationDataSource.getLocation(location.id)

            MatcherAssert.assertThat(actual, CoreMatchers.notNullValue())

        }
    }

    class GetAllTest {

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
            val actual = locationDataSource.getLocations().first()

            MatcherAssert.assertThat(actual, CoreMatchers.notNullValue())
        }
    }

    class GetLocationTest {
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

            val actual = locationDataSource.getLocation(location.id)!!

            MatcherAssert.assertThat(actual.id, CoreMatchers.`is`(location.id))
            MatcherAssert.assertThat(actual.title, CoreMatchers.`is`(location.title))
            MatcherAssert.assertThat(actual.description, CoreMatchers.`is`(location.description))
            MatcherAssert.assertThat(actual.locationName, CoreMatchers.`is`(location.locationName))
            MatcherAssert.assertThat(actual.longitude, CoreMatchers.`is`(location.longitude))
            MatcherAssert.assertThat(actual.latitude, CoreMatchers.`is`(location.latitude))
        }
    }

    class DeleteTest {
        @Before
        fun setUp() = runTest {
            locationDao.insert(location)
        }

        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {
            locationDataSource.delete(location)

            val actual = locationDao.getLocation(location.id)

            MatcherAssert.assertThat(actual, CoreMatchers.nullValue())
        }
    }
}