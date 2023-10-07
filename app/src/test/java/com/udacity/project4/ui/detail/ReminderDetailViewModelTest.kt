package com.udacity.project4.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.Geofence
import com.udacity.project4.data.repository.location.RemindersLocalRepository
import com.udacity.project4.domain.location.LocationUseCaseImpl
import com.udacity.project4.ui.FakeDataSource
import com.udacity.project4.ui.reminder.ReminderDetailViewModel
import com.udacity.project4.ui.reminders.getOrAwaitValue
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
class ReminderDetailViewModelTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var reminderDetailViewModel: ReminderDetailViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        remindersLocalRepository = FakeDataSource()
        (remindersLocalRepository as FakeDataSource).deleteAll()
        reminderDetailViewModel = ReminderDetailViewModel(LocationUseCaseImpl(remindersLocalRepository))
    }

    @After
    fun teaDown() {
        (remindersLocalRepository as FakeDataSource).deleteAll()
        (remindersLocalRepository as FakeDataSource).setError(false)
    }

    @Test
    fun save_location() = runTest(UnconfinedTestDispatcher()) {
        val expected = "expected"
        var actual = ""
        val callBack: (geofence: Geofence) -> Unit = {
            actual = expected
        }

        reminderDetailViewModel.saveLocation(callBack)

        assertThat(
            reminderDetailViewModel.showToast.getOrAwaitValue(),
            `is`("Save location success")
        )
        assertThat(
            reminderDetailViewModel.showLoading.getOrAwaitValue(),
            `is`(false)
        )
        assertThat(actual, `is`(expected))
    }
}