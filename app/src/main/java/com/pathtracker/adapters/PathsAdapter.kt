package com.pathtracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pathtracker.data.entities.PathEntity
import com.pathtracker.databinding.ItemPathBinding
import java.text.SimpleDateFormat
import java.util.*

class PathsAdapter(
    private val onPathClick: (PathEntity) -> Unit
) : ListAdapter<PathEntity, PathsAdapter.PathViewHolder>(PathDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val binding = ItemPathBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PathViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PathViewHolder(private val binding: ItemPathBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(path: PathEntity) {
            binding.apply {
                tvPathName.text = path.name
                tvPathDescription.text = if (path.description.isNotBlank()) path.description else "No description"

                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                tvStartTime.text = "Started: ${dateFormat.format(Date(path.startTime))}"

                val endText = if (path.endTime != null) {
                    "Ended: ${dateFormat.format(Date(path.endTime))}"
                } else {
                    "In Progress"
                }
                tvEndTime.text = endText

                tvDistance.text = String.format("%.2f km", path.totalDistance)
                ratingBar.rating = path.rating

                root.setOnClickListener { onPathClick(path) }
            }
        }
    }

    class PathDiffCallback : DiffUtil.ItemCallback<PathEntity>() {
        override fun areItemsTheSame(oldItem: PathEntity, newItem: PathEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PathEntity, newItem: PathEntity): Boolean {
            return oldItem == newItem
        }
    }
}