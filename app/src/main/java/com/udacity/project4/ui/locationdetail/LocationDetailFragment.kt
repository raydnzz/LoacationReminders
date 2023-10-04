package com.udacity.project4.ui.locationdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.databinding.FragmentLocationDetailBinding
import com.udacity.project4.receiver.LocationReceiver
import com.udacity.project4.ui.dto.LocationSelectDto
import com.udacity.project4.utils.Constants
import com.udacity.project4.utils.PermissionUtil.getPermission
import com.udacity.project4.utils.PermissionUtil.isPermission
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class LocationDetailFragment : Fragment() {

    companion object {
        const val LOCATION_SELECT_KEY = "LOCATION_SELECT_KEY"
    }

    private val intentLocation by lazy {
        Intent(activity, LocationReceiver::class.java).run {
            action = Constants.ACTION_LOCATION_REMINDER
            PendingIntent.getBroadcast(
                requireActivity(), 0, this, PendingIntent.FLAG_MUTABLE
            )
        }
    }

    private val geofenceClient by lazy {
        LocationServices.getGeofencingClient(requireActivity())
    }

    private val viewModel: LocationDetailViewModel by viewModel()
    private val args: LocationDetailFragmentArgs by navArgs()

    private val binding by lazy {
        FragmentLocationDetailBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.lifecycleOwner = requireActivity()
            it.executePendingBindings()
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            for (key in it.keys) {
                if (it[key] == false) {
                    showSettingSystem()
                    return@registerForActivityResult
                }
            }
            initRequest()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args.location?.let {
            viewModel.apply {
                title.value = it.title
                description.value = it.description
                locationDto.value = LocationSelectDto(
                    name = it.locationName, latitude = it.latitude, longitude = it.longitude
                )
                setLocation(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry?.let {
            it.savedStateHandle.getLiveData<Bundle>(LOCATION_SELECT_KEY).observe(it) { result ->
                val selectDto =
                    BundleCompat.getParcelable(result, "DATA", LocationSelectDto::class.java)
                viewModel.locationDto.value = selectDto
            }
        }

        binding.apply {
            lnSelector.setOnClickListener {
                LocationDetailFragmentDirections.actionLocationDetailFragmentToMapFragment().also {
                    findNavController().navigate(it)
                }
            }

            btnSave.setOnClickListener {
                addLocationReminder()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.showToast.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.showLoading.observe(viewLifecycleOwner) {
            binding.viewLoading.loadingScreen.isVisible = it
        }
    }

    private fun addLocationReminder() {
        var permissions = getPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }

        if (!isPermission(*permissions)) {
            activityResultLauncher.launch(permissions)
        } else {
            initRequest()
        }
    }

    private fun initRequest() {
        viewModel.showLoading.value = true
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
                    save()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun save() {
        viewModel.saveLocation { geofence ->
            val request = createGeofencingRequest(geofence = geofence)
            geofenceClient.addGeofences(request, intentLocation).run {
                addOnFailureListener {
                    Log.d("addGeofences", "${it.message}")
                }
                addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("addGeofences", "addGeofenceForReminder: ${geofence.requestId}")
                    }
                }
            }
            findNavController().popBackStack()
        }
    }

    private fun createGeofencingRequest(geofence: Geofence) =
        GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence).build()


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
}
