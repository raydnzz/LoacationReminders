package com.udacity.project4.ui.locations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.databinding.ItemLocationBinding

class LocationAdapter(
    private val itemListener: (location: Location) -> Unit
) : ListAdapter<Location, LocationAdapter.LocationHolder>(diffUtil) {

    inner class LocationHolder(private val binding: ItemLocationBinding) :
        ViewHolder(binding.root) {
        fun bind(location: Location) {
            binding.location = location
            binding.executePendingBindings()

            binding.item.setOnClickListener {
                itemListener.invoke(location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        return LocationHolder(
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Location>() {
            override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
                return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.locationName == newItem.locationName
            }

            override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
                return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.locationName == newItem.locationName
            }
        }
    }
}
