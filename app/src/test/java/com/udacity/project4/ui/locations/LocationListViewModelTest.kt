package com.udacity.project4.ui.locations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.domain.location.LocationUseCaseImpl
import com.udacity.project4.ui.FakeDataSource
import com.udacity.project4.utils.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class LocationListViewModelTest {

    private lateinit var locationRepository: FakeDataSource
    private lateinit var locationListViewModel: LocationListViewModel

    private val location = Location(
        id = "15",
        title = "Test",
        description = "description",
        locationName = "locationName",
        longitude = 3.2,
        latitude = 3.4
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        locationRepository = FakeDataSource()
        locationRepository.deleteAll()
        locationListViewModel = LocationListViewModel(LocationUseCaseImpl(locationRepository))
    }

    @After
    fun teaDown() {
        locationRepository.deleteAll()
        locationRepository.setError(false)
    }

    @Test
    fun test_has_data() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())
        locationRepository.add(location)

        locationListViewModel.getLocation()
        assertThat(locationListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        advanceUntilIdle()
        assertThat(locationListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        val actual = locationListViewModel.locations.getOrAwaitValue()
        assertThat(actual, notNullValue())
    }

    @Test
    fun test_no_data_with_error() = runTest(mainCoroutineRule.testDispatcher) {
        locationRepository.setError(true)
        locationListViewModel.getLocation()
        val actual = locationListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(actual, `is`("GetLocation List Error"))
    }

    @Test
    fun test_no_data_with_data_empty() = runTest(mainCoroutineRule.testDispatcher) {
        locationListViewModel.getLocation()
        val actual = locationListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(actual, `is`("Empty"))
    }
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2, timeUnit: TimeUnit = TimeUnit.SECONDS, afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST") return data as T
}
