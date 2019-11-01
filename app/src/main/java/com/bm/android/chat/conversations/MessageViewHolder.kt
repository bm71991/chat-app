package com.bm.android.chat.conversations

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage

class MessageViewHolder(itemView:View)
    : RecyclerView.ViewHolder(itemView) {
    private val messageView = itemView.findViewById<TextView>(R.id.message)

    fun bindData(messageData: ChatMessage) {
        messageView.text = messageData.message
    }
}