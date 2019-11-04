package com.bm.android.chat.conversations.conversation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.conversations.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

abstract class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bindData(model: ChatMessage)

    fun getDateString(date: Date):String    {
        val dateFormat = SimpleDateFormat("EEE, h:mm a")
        return dateFormat.format(date)
    }
}