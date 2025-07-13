package com.universidad.mindsparkai.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.universidad.mindsparkai.data.models.ChatMessage
import com.universidad.mindsparkai.databinding.ItemChatMessageBinding
import com.universidad.mindsparkai.databinding.ItemTypingIndicatorBinding

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    private var isTyping = false

    companion object {
        private const val TYPE_USER_MESSAGE = 0
        private const val TYPE_AI_MESSAGE = 1
        private const val TYPE_TYPING = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && isTyping) {
            TYPE_TYPING
        } else {
            val message = getItem(position)
            if (message.isFromUser) TYPE_USER_MESSAGE else TYPE_AI_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isTyping) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TYPING -> {
                val binding = ItemTypingIndicatorBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TypingViewHolder(binding)
            }
            else -> {
                val binding = ItemChatMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MessageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> {
                if (position < currentList.size) {
                    holder.bind(getItem(position))
                }
            }
            is TypingViewHolder -> {
                // Typing indicator doesn't need binding
            }
        }
    }

    fun setTyping(typing: Boolean) {
        val wasTyping = isTyping
        isTyping = typing

        if (wasTyping != typing) {
            if (typing) {
                notifyItemInserted(itemCount - 1)
            } else {
                notifyItemRemoved(itemCount)
            }
        }
    }

    class MessageViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.message = message
            binding.executePendingBindings()
        }
    }

    class TypingViewHolder(binding: ItemTypingIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}

