package com.udacity.project4.data.database.daos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.udacity.project4.data.database.AppDatabase
import com.udacity.project4.data.database.entites.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
@ExperimentalCoroutinesApi
@SmallTest
class LocationDaoTest {
    private companion object {
        val context: Context = ApplicationProvider.getApplicationContext()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val locationDao = db.getLocationDao()
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
            locationDao.insert(location)

            val actual = locationDao.getLocation(location.id)

            assertThat(actual, notNullValue())

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
            val actual = locationDao.getAll()

            assertThat(actual, notNullValue())
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

            val actual = locationDao.getLocation(location.id)!!

            assertThat(actual.id, `is`(location.id))
            assertThat(actual.title, `is`(location.title))
            assertThat(actual.description, `is`(location.description))
            assertThat(actual.locationName, `is`(location.locationName))
            assertThat(actual.longitude, `is`(location.longitude))
            assertThat(actual.latitude, `is`(location.latitude))
        }
    }


    class DeleteTest {
        @Before
        fun setUp() = runTest {
            locationDao.insert(location)
        }

        @Test
        fun test() = runTest(UnconfinedTestDispatcher()) {
            locationDao.delete(location)

            val actual = locationDao.getLocation(location.id)

            assertThat(actual, nullValue())
        }
    }
}
