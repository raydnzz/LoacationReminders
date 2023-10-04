package com.udacity.project4.ui.locations

import android.app.Activity
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.data.database.daos.LocationDao
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.di.appModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito
import org.mockito.Mockito.after


@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class LocationListFragmentTest : KoinTest {

    @JvmField
    @Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val applicationContext: Context = ApplicationProvider.getApplicationContext()
    private val mockNavController = Mockito.mock(NavController::class.java)
    private lateinit var viewModel: LocationListViewModel
    private lateinit var locationDao: LocationDao
    private val location = Location(
        id = "15",
        title = "Test",
        description = "description",
        locationName = "locationName",
        longitude = 3.2,
        latitude = 3.4
    )


    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            androidContext(applicationContext)
            modules(appModule)
        }

        locationDao = get()
        viewModel = get()
    }

    @After
    fun teaDown() {
        stopKoin()
    }

    // Test for display snack bar
    @Test
    fun test_display_no_data() {
        launchFragmentInContainer<LocationListFragment>(bundleOf(), R.style.LoacationRemindersTheme)
        onView(withText("No Data")).check(matches(isDisplayed()))
        onView(withText("Locations empty")).check(matches(isDisplayed()))
    }

    @Test
    fun test_display_has_data() {
        runBlocking {
            locationDao.insert(location)
        }

        val spec = launchFragmentInContainer<LocationListFragment>(
            bundleOf(), R.style.LoacationRemindersTheme
        )

        onView(withText("Get location success")).inRoot(
            withDecorView(
                not(
                    `is`(
                        getActivity(spec)?.window?.decorView
                    )
                )
            )
        ).check(matches(isDisplayed()))
        onView(withText(location.title)).check(matches(isDisplayed()))
        onView(withText(location.locationName)).check(matches(isDisplayed()))

    }

    @Test
    fun test_navigate_to_location_detail() {
        launchFragmentInContainer<LocationListFragment>(
            bundleOf(), R.style.LoacationRemindersTheme
        ).onFragment {
            Navigation.setViewNavController(
                it.requireView(), mockNavController
            )
        }

        Thread.sleep(4000)

        onView(withId(R.id.btn_add)).perform(click())
        Mockito.verify(mockNavController, after(1000))
            .navigate(LocationListFragmentDirections.actionLocationListFragmentToLocationDetailFragment())
    }

    private fun getActivity(fragmentScenario: FragmentScenario<LocationListFragment>): Activity? {
        var activity: Activity? = null
        fragmentScenario.withFragment {
            activity = this@withFragment.requireActivity()
        }
        return activity
    }
}

