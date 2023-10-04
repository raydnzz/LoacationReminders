package com.udacity.project4.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentLocationListBinding
import com.udacity.project4.ui.locations.adapter.LocationAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationListFragment : Fragment(), MenuProvider {

    private val binding by lazy {
        FragmentLocationListBinding.inflate(layoutInflater)
    }

    private val viewModel: LocationListViewModel by viewModel()

    private val locationAdapter = LocationAdapter {

    }

    private val menuHost: MenuHost by lazy {
        requireActivity()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.locations.observe(viewLifecycleOwner) {
            binding.groupNoData.isVisible = it.isEmpty()
            binding.rvListLocation.isVisible = it.isNotEmpty()
            locationAdapter.submitList(it)
        }

        binding.apply {
            btnAdd.setOnClickListener {
                LocationListFragmentDirections.actionLocationListFragmentToLocationDetailFragment()
                    .let { action ->
                        findNavController().navigate(action)
                    }
            }

            rvListLocation.adapter = locationAdapter
            rvListLocation.layoutManager = LinearLayoutManager(requireContext())

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.showSnackBar.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.showLoading.observe(viewLifecycleOwner) {
            binding.viewLoading.loadingScreen.isVisible = it
        }

        viewModel.showToast.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity().applicationContext, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        menuHost.addMenuProvider(this, viewLifecycleOwner)
        viewModel.getLocation()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.location_list, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.logout) {
            AuthUI.getInstance().signOut(requireContext())
        }
        return false
    }
}