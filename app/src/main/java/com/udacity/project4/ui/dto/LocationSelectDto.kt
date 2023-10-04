package com.udacity.project4.ui.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationSelectDto(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) : Parcelable {
    override fun toString() = name
}

