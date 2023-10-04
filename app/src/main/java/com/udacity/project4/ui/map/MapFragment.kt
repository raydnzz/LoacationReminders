package com.udacity.project4.ui.map

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentMapBinding
import com.udacity.project4.ui.locationdetail.LocationDetailFragment
import com.udacity.project4.utils.PermissionUtil.checkPermission
import com.udacity.project4.utils.PermissionUtil.getPermission
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class MapFragment : Fragment(), MenuProvider {

    private val binding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val viewModel: MapViewModel by viewModels()

    private lateinit var googleMap: GoogleMap
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val menuHost: MenuHost by lazy {
        requireActivity()
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            for (key in it.keys) {
                if (it[key] == false) {
                    showSettingSystem()
                    return@registerForActivityResult
                }
            }
            updateLocation()
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment)?.run {
            getMapAsync {
                googleMap = it
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )
                setMapEvents()
                updateLocation()
            }
        }

        binding.btnSave.setOnClickListener {
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle[LocationDetailFragment.LOCATION_SELECT_KEY] =
                    bundleOf("DATA" to viewModel.locationSelectDto)
                popBackStack()
            }
        }
    }

    private fun updateLocation() {
        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
            val requestLocation =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).run {
                    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    setDurationMillis(TimeUnit.MINUTES.toMillis(5))
                    setWaitForAccurateLocation(true)
                    setMaxUpdates(1)
                    build()
                }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(requestLocation)
            val settingsClient = LocationServices.getSettingsClient(requireActivity())
            settingsClient.checkLocationSettings(builder.build()).apply {
                addOnCompleteListener {
                    if (it.isSuccessful) {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc: Location? ->
                            loc?.run {
                                LatLng(latitude, longitude).also { latLng ->
                                    addLocation(latLng)
                                    googleMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            latLng,
                                            20f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            activityResultLauncher.launch(getPermission())
        }

    }

    private fun setMapEvents() {
        googleMap.apply {
            setOnPoiClickListener {
                addMarker(MarkerOptions().position(it.latLng).title(it.name))?.let { marker ->
                    viewModel.updateMarkerAndPointOfInterest(marker, it)
                }
            }

            setOnMapClickListener { addLocation(it) }
        }
    }

    private fun addLocation(latLng: LatLng) {
        lifecycleScope.launch {
            val addresses = getAddress(latLng)
            if (addresses.isNullOrEmpty()) return@launch
            val address = addresses.first()
            PointOfInterest(latLng, "", address.getAddressLine(0)).let { pointOfInterest ->
                googleMap.addMarker(
                    MarkerOptions().position(pointOfInterest.latLng)
                        .title(pointOfInterest.name)
                )?.let { marker ->
                    viewModel.updateMarkerAndPointOfInterest(marker, pointOfInterest)
                }
            }
        }
    }

    private suspend fun getAddress(latLng: LatLng): List<Address>? = suspendCancellableCoroutine {
        Geocoder(requireContext()).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(
                    latLng.latitude, latLng.longitude, 1
                ) { addresses ->
                    it.resume(value = addresses)
                }
            } else {
                @Suppress("DEPRECATION") getFromLocation(
                    latLng.latitude, latLng.longitude, 1
                ).also { addresses ->
                    it.resume(value = addresses)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        menuHost.addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.map_style, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
        R.id.normal -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }

        R.id.hybrid -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }

        R.id.satellite -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }

        R.id.terrain -> {

            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }

        else -> false
    }

    private fun showSettingSystem() {
        Snackbar.make(requireView(), "The Location Permission Denied", Snackbar.LENGTH_SHORT)
            .setAction("Change") {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    it.data = Uri.fromParts("package", requireActivity().packageName, null)
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(it)
                }
            }.show()

    }


    override fun onPause() {
        super.onPause()
        menuHost.removeMenuProvider(this)
    }
}