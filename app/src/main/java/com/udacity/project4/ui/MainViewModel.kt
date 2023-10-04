package com.udacity.project4.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.ui.dto.FirebaseUserLiveData

class MainViewModel : ViewModel() {

    private val _firebaseUser = FirebaseUserLiveData()
    val isLogin: LiveData<Boolean> = _firebaseUser.map { it != null }
}