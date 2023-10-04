package com.udacity.project4.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.Geofence
import com.udacity.project4.data.repository.location.LocationRepository
import com.udacity.project4.domain.location.LocationUseCaseImpl
import com.udacity.project4.ui.FakeDataSource
import com.udacity.project4.ui.locationdetail.LocationDetailViewModel
import com.udacity.project4.ui.locations.getOrAwaitValue
import com.udacity.project4.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationDetailViewModelTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var locationDetailViewModel: LocationDetailViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        locationRepository = FakeDataSource()
        (locationRepository as FakeDataSource).deleteAll()
        locationDetailViewModel = LocationDetailViewModel(LocationUseCaseImpl(locationRepository))
    }

    @After
    fun teaDown() {
        (locationRepository as FakeDataSource).deleteAll()
        (locationRepository as FakeDataSource).setError(false)
    }

    @Test
    fun save_location() = runTest(UnconfinedTestDispatcher()) {
        val expected = "expected"
        var actual = ""
        val callBack: (geofence: Geofence) -> Unit = {
            actual = expected
        }

        locationDetailViewModel.saveLocation(callBack)

        assertThat(
            locationDetailViewModel.showToast.getOrAwaitValue(),
            `is`("Save location success")
        )
        assertThat(
            locationDetailViewModel.showLoading.getOrAwaitValue(),
            `is`(false)
        )
        assertThat(actual, `is`(expected))
    }
}