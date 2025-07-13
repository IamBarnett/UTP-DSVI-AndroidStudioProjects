package com.universidad.mindsparkai.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.universidad.mindsparkai.databinding.ItemRecentActivityBinding

data class RecentActivity(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val type: String // "summary", "quiz", "chat", "plan"
)

class RecentActivityAdapter(
    private val onItemClick: (RecentActivity) -> Unit
) : ListAdapter<RecentActivity, RecentActivityAdapter.ViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentActivityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemRecentActivityBinding,
        private val onItemClick: (RecentActivity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: RecentActivity) {
            binding.tvTitle.text = activity.title
            binding.tvSubtitle.text = activity.subtitle
            binding.tvTimestamp.text = activity.timestamp

            // Set icon based on type
            val iconRes = when (activity.type) {
                "summary" -> "📄"
                "quiz" -> "🧠"
                "chat" -> "💬"
                "plan" -> "📅"
                else -> "📋"
            }
            binding.tvIcon.text = iconRes

            binding.root.setOnClickListener { onItemClick(activity) }
        }
    }
}

class ActivityDiffCallback : DiffUtil.ItemCallback<RecentActivity>() {
    override fun areItemsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
        return oldItem == newItem
    }
}

